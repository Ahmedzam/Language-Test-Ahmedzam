# framework-ega-codegen

Contains supporting files used to generate API classes using [Swagger codegen](https://github.com/swagger-api/swagger-codegen).

Run com.ibm.watson.app.common.codegen.languages.WatsonAppJaxRSServerCodegen to generate Java source files.
The templates for code generation are defined in src/main/resources/WatsonAppJaxRS.

See /watson-app-qaclassifier-war/pom.xml for an example of how to integrate code generation into a Maven build.