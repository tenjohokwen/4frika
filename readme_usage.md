
This documentation is relevant when you have used this project as an artifact and you want to quickly start a project
=====================================================================================================================

1. Remove all comments having the following tag: TODO4frika

2. Ensure you have the right db schema/name configured

3. Put a gitignore entry for the following file "application-dev.yml"

4. control sql logging using the foll
    * logging.level.jdbc.sqlonly: ERROR
    * logging.level.jdbc.sqltiming: ERROR
    * logging.level.jdbc.audit: ERROR
    * logging.level.jdbc.resultset: ERROR
    * logging.level.jdbc.connection: ERROR
    * logging.level.org.hibernate.engine: ERROR
    * logging.level.org.hibernate.loader: ERROR
    * logging.level.org.hibernate.hql: ERROR




TIPS
====

* When using a db versioning tool like flyway, avoid writing tests like below
```
    @Test
    @Sql(scripts = "/prefixed_schema-all.sql")
    public void testUpdateBox() throws Exception {
        List<Box> boxes = (List<Box>) boxRepository.findAll();
        int initialCount = boxes.size();
        boxRepository.delete(5l);
        boxes = (List<Box>) boxRepository.findAll();
        assertTrue(boxes.size() == (initialCount - 1));
    }
```

* The problem with the above is that you now have two sources of scripts (the idea is to avoid multiple reasons for failure and code duplication)
  * Those under src/main/resources/db/migration
  * src/main/resources/prefixed_schema-all.sql

* In addition,at the moment spring will not execute the above sql once flyway is on the classpath. see https://github.com/spring-projects/spring-boot/issues/2753



Configs
=======

Flyway
------
* Dependencies
* Added maven plugin which will do migrations and corresponding flyway-[env].properties files for portability's sake(src/main/resources/config)
* The default location of the configuration file is the same same folder as the pom, that's why I had to explicitly specify one located in the resources folder
* Used default location for migration scripts (src/main/resources/db/migration)
* Used default versioning Vxx
* Added flyway test lib (runs migration scripts before each test thanks to @FlywayTest annotation and FlywayTestExecutionListener)
* Added flyway-dbunit-spring4-test lib for dbunit integration and it needs @FlywayTest, @DBUnitSupport and the following listeners:
    * DbUnitTestExecutionListener.class
    * DependencyInjectionTestExecutionListener.class
    * FlywayDBUnitTestExecutionListener.class (this is a superset of FlywayTestExecutionListener)
* see https://github.com/flyway/flyway-test-extensions/blob/master/flyway-test-extensions/flyway-test-samples/flyway-test-dbunit-samples/flyway-test-sample-dbunit-spring4/src/test/java/org/flywaydb/test/sample/spring4/Spring4DBunitTest.java


logging
-------
* I opted to use my own config because I tried to reuse that offered by spring and customize (I had to change the whole file-appender) but I did not get exactly what I wanted
* Logback has a short cut name for two formats for access logging: "common" and "combined"
* Sample "common" format (%h %l %u [%t] "%r" %s %b)
 ```
      0:0:0:0:0:0:0:1 - - [16/Jan/2016:00:24:04 +0100] "GET / HTTP/1.1" 404 306
 ```

    * h = client host
    * l = Remote log name. In logback-access, this converter always returns the value "-"
    * u = Remote user.
    * t = date (it defaults to %t{dd/MMM/yyyy:HH:mm:ss Z})
    * r = request url
    * s = status code of request
    * b = Response's content length. (all names b / B / bytesSent)

* the "combined" format is like so... %h %l %u [%t] "%r" %s %b "%i{Referer}" "%i{User-Agent}"

* Configure for jmx by adding the following to the configuration element

        <jmxConfigurator/>
        <contextName>smartix</contextName>

spring base appender
--------------------

```
        <included>
            <include resource="org/springframework/boot/logging/logback/defaults.xml" />
            <property name="LOG_FILE" value="${LOG_FILE:-${LOG_PATH:-${LOG_TEMP:-${java.io.tmpdir:-/tmp}}/}spring.log}"/>
            <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
            <include resource="org/springframework/boot/logging/logback/file-appender.xml" />
            <root level="INFO">
                <appender-ref ref="CONSOLE" />
                <appender-ref ref="FILE" />
            </root>
        </included>
```

config
-------

In application.yml

```
    #logging
    # default location is directly in the classpath
    logging.config: classpath:config/logback/logback.xml
```
* There is also a logback-dev.xml which is defined in application-dev.yml:

```
logging.config: classpath:config/logback/logback-dev.xml
```

* The "logback-dev.xml" also logs to the console whereas the default logback.xml just to a file
* I added 'class="net.logstash.logback.encoder.LogstashEncoder"' to the rollingAppender but this is completely optional
* The logstashEncoder requires "logstash-logback-encoder" on the class path
* 'logstash-logback-encoder' is actually intended to be used for json(ing) log data (not yet versed with its usage but it does not harm to have it)
* Logback config makes use of application-commons.properties for some its placeholders [see](http://stackoverflow.com/questions/29322709/unable-to-use-spring-property-placeholders-in-logback-xml)



Auditing
--------
* Added the following
    * AbstractAuditingEntity
    * AuditingDateTimeProvider
    * DateTimeService
    * DateTimeServiceImpl
    * SpringSecurityAuditorAware
    * Add "EnableJpaAuditing" to your persistence config / db config, [@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware", dateTimeProviderRef = AuditingDateTimeProvider.NAME)]
* Libs added
    * joda-time
    * usertype.core (jadira)
    * hibernate-envers
    * hibernate-validator
    * joda-time-hibernate
    * spring-boot-starter-tomcat
* Add extends clause to entity like so "extends AbstractAuditingEntity"

####hibernate auditing
* You only need to have an entity with a primary key and use the annotation @Audited.
* You can use the annotation @Audited either at the top of the class or only in the fields that you would like to audit.
* NB If your mapped super type is not @Audited, you will not get its fields audited by hibernate
* In other words @Audited needs to be both on the Mapped super class as well as the entity extending the mapped super class
* Once you add the @Audited annotation you will see that a new table with the suffix “_AUD” will be created for each entity.
* Also you will find that a new table called REVINFO, which contains all the revisions information, has been created.
* using the sqldelete clause on entities causes them not to be versioned (audited) by hibernate [@SQLDelete(sql="UPDATE box SET deleted = '1' WHERE id = ?")]


Date zone
---------

* Converting db UTC to zoned time (config)
      spring.jpa.properties.jadira.usertype:
        autoRegisterUserTypes: true
        javaZone: ${app.zone}
        databaseZone: ${app.zone}

```
@Column
@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
private DateTime dateTime;
```
`

* For java8 date time lib the following works with postgres but not with mysql

```
@Column(name = "creation_time", nullable = false)
@Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
@CreatedDate
private ZonedDateTime creationTime;
``

Profiles
---------
* Tests add the 'test' profiles by themselves see for example @ActiveProfiles(value = {"test"}) of DBUnitTest class
* Other profiles are added through the following config in application.yml
  ``
  spring.profiles.include: commons,local
  ```
* 'commons' is for shared config
* 'local' is actually meant to be for local config.  use it to define your dev profile <code>spring.profiles.include: dev</code>
* In the dev env, during tests the following profiles should be active: dev,commons,local,test
* The main thread will produce the following log "The following profiles are active: dev,commons,local,test"



testng and junit
----------------

            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.19.1</version>
                <configuration>
                    <properties>
                        <property>
                            <name>junit</name> <!-- do not use testng to run junit -->
                            <value>false</value>
                        </property>
                    </properties>
                    <threadCount>1</threadCount> <!-- number of parallel threads for the tests. testng feature -->
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.apache.maven.surefire</groupId>
                        <artifactId>surefire-junit47</artifactId>
                        <version>2.19.1</version>
                    </dependency>
                    <dependency>
                        <groupId>org.apache.maven.surefire</groupId>
                        <artifactId>surefire-testng</artifactId>
                        <version>2.19.1</version>
                    </dependency>
                </dependencies>
            </plugin>

* The above configuration of the maven-surefire-plugin makes it possible for both testng and junit tests to be run by maven on the command line
* The above configuration is the minimal basic that works in this env
* See [link](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html). Note that not just the config above worked for this env
