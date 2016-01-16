* This list is divided into 2 checks during context startup and checks after full deployment

checks during context startup
-----------------------------

* These will be programmatically checked:
    * Flyway migrations
    * Health check (heart beat of dependencies)


checks after full deployment
----------------------------

* These are checked after deployment
   * Health checks
   * The deployed profile
   * App Version
   * Build version
   * Branch
   * If possible a complete flow (this is a test with dummy objects on live)