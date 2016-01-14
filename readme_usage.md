
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