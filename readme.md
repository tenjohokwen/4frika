Db init
--------

* Spring boot picks up 'schema-all.sql' from class path root and executes

**Config**

#Features

###Db schema creation and population
* schema-all.sql  //to init the schema
* data-all.sql    // This one is not used here but if a data-all.sql file exists the db will be automatically populated


###Soft delete
* Using hibernate


###Tomcat datasource
* This is automatically configured by spring


###Db
* mysql is used as the db here




* Spring boot picks up schema-${platform}.sql and data-${platform}.sql files (if present), where platform is the value of spring.datasource.platform
* When using jpa you need to set the following in order to avoid conflicts with hibernate


    spring.jpa.hibernate.ddl-auto: none


* examples of the platform you could set are
    * hsqldb,
    * h2,
    * oracle,
    * mysql,
    * postgresql

* Use schema-all.sql' if you do not want to declare the platform


#Soft delete implementation (using hibernate)

    @SQLDelete(sql="UPDATE box SET deleted = '1' WHERE id = ?")
    @Where(clause="deleted <> '1'")
    public class Box implements Serializable {
        ...
    }

