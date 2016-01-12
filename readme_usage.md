
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
