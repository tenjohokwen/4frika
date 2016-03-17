* metrics
   * General
   * threadpools (InstrumentedHttpRequestExecutor from dropwizard,InstrumentedExecutorService, InstrumentedScheduledExecutorService)
   * clients (resttemplate, httpClient)
* CORS
* hysterix
* retry lib
* security
* General auditing see:
     * smartix CustomAuditEventRepository, PersistenceAuditEventRepository, PersistentTokenRepository, AuditEventConverter
     * https://github.com/charlesrgould/authentication-audit/blob/master/src/main/java/example/AuditEventListener.java (I have forked it)
* app info
* update parent pom

* clean base pom
    * add repos and make sure the spring repo is http://repo.spring.io/libs-release
    * ensure all snapshots are disabled
    * update plugins to the latest versions
    * fix git-commit-id-plugin(see 4frika pom)
    * add properties-maven-plugin (see 4frika pom)
    * ensure no conflicts with spring-boot parent pom
    * dependencies already in spring-boot parent pom or base pom should be used without specifying their versions (assuming they are managed)
    * configure maven-compiler-plugin to use [errorprone](https://github.com/google/error-prone)
    * pit [plugin](http://pitest.org/quickstart/maven/)
    * if you have time add [jqassistant](http://buschmais.github.io/jqassistant/doc/1.1.0/)

* TomcatGaugeSet (from spaceman)

Exception handling
-------------------
* core exception: 4frikaException (runtimeException, protected field "HELP_CODE" UUID that is generated when exception is created and final)
* override the getMessage in every impl
* "HELP_CODE" should be available in getMessage => logs should all contain the HELP_CODE UUID
* See ErrorMessage (and CheckoutApiExceptionHandler) of spaceman for ideas on how to prepare client friendly message

touched
--------

* logging (timezone on tomcat not done)
* auditing (db tables audit done but no event logging as yet)
* flyway dbunit and hikari
* log4jdbc


duplicate checker plugin
------------------------

            <plugin>
                <groupId>com.ning.maven.plugins</groupId>
                <artifactId>maven-duplicate-finder-plugin</artifactId>
                <version>${basepom.plugin.duplicate-finder.version}</version>
                <configuration>
                    <skip>false</skip>
                    <failBuildInCaseOfConflict>true</failBuildInCaseOfConflict>
                    <ignoredDependencies>
                        <dependency>
                            <!-- seems to have some duplicates with hibernate-jpa-2.1-api but should be ignored-->
                            <groupId>org.apache.tomcat.embed</groupId>
                            <artifactId>tomcat-embed-core</artifactId>
                        </dependency>
                    </ignoredDependencies>
                    <ignoredResources>
                        <resource>META-INF/spring.factories</resource>
                        <resource>META-INF/spring.provides</resource>
                        <resource>META-INF/additional-spring-configuration-metadata.json</resource>
                        <resource>META-INF/spring-configuration-metadata.json</resource>
                        <resource>META-INF/web-fragment.xml</resource>
                        <resource>changelog.txt</resource>
                    </ignoredResources>
                </configuration>
            </plugin>


run maven with alternative central repo
-----------------------------------------

* By default, the central repo used is the one configured in settings.xml
* the settings.xml has also been configured in such a way that an alternate repo may be used
* Give in a cmd and append to it " -P  \!groupon" e.g

```
 mvn duplicate-finder:check  -P  \!groupon
```

* This means that intellij idea will be using the groupon repo (since the soln above is to use the cmd line)


Logging
-------
add logging in such a way that you can inspect difficult areas in the code on production by changing the log level using jconsole