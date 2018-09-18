# Server-Side Loyalty Rewards Program

## Build instructions:
1) Prior to build bring up a PostgreSQL instance and update the authentication credentials in src/test/resources/jdbc.properties
2) Run maven with `mvn package`

Maven will build, run tests and create a WAR file which can be deployed to any Tomcat instance.
