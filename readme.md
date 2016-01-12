

1. Profile Config
-----------------
* create the file application-dev.yml (this file should not be checked in)
* put <code>spring.profiles.active: dev</code> within (else configure using -Dspring.profiles.active=dev (VM options))

2. Db init
-----------

* mvn compile flyway:migrate -Pdev




