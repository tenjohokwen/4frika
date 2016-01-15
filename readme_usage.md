
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
* Used default location for migration scripts (src/main/resources/db/migration)
* Used default versioning Vxx
* Added flyway test lib (runs migration scripts before each test thanks to @FlywayTest annotation and FlywayTestExecutionListener)
* Added flyway-dbunit-spring4-test lib for dbunit integration and it needs @FlywayTest, @DBUnitSupport and the following listeners:
    * DbUnitTestExecutionListener.class
    * DependencyInjectionTestExecutionListener.class
    * FlywayDBUnitTestExecutionListener.class (this is a superset of FlywayTestExecutionListener)
* see https://github.com/flyway/flyway-test-extensions/blob/master/flyway-test-extensions/flyway-test-samples/flyway-test-dbunit-samples/flyway-test-sample-dbunit-spring4/src/test/java/org/flywaydb/test/sample/spring4/Spring4DBunitTest.java