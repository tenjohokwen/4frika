
0. Add scm to pom
-----------------
* In the pom file add the scm so that it points to the repository of the current project
* If the scm is not added, releases and deployments will attempt to take the one in the parent pom


1. Profile Config
-----------------
* create the file application-dev.yml (this file should not be checked in)
* put <code>spring.profiles.active: dev</code> within (else configure using -Dspring.profiles.active=dev (VM options))

2. Db init
-----------

* mvn compile flyway:migrate -Pdev




