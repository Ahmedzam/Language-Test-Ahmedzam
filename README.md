# framework-ega
A project containing common classes and utilities used by the Watson Developer Cloud Gallery Applications.

## Build
Run `mvn install` to build the project.

## framework-ega-codegen
Contains supporting files used to generate API classes using Swagger codegen.

More information in the [project README](framework-ega-codegen/README.md)

## framework-ega-infra
Contains common code for interacting with Bluemix services, including

* Wrapper/helper classes for the NL Classifier service in com.ibm.watson.app.common.services.nlclassifier
* Wrapper/helper classes for the SQLDB service in com.ibm.watson.app.common.services.sqldb
* JPA persistence helper classes
* Classes used to configure an application based on the VCAP_SERVICES environment variable set by Bluemix

## framework-ega-test
Contains common test classes.

The classes in this project can be packaged into a WAR and used as a mock NL Classifier service.

More information in the [project README](framework-ega-test/README.md)

## framework-ega-tools
Contains tools and utilities that can be used during development or to manage a running application.

More information in the [project README](framework-ega-tools/README.md)