* metrics
   * General
   * threadpools
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


touched
--------

* logging (timezone on tomcat not done)
* auditing (db tables audit done but no event logging as yet)
* flyway dbunit and hikari
* log4jdbc