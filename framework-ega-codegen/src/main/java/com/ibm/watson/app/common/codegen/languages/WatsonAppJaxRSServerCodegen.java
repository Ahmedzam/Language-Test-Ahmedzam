/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.common.codegen.languages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;

import com.wordnik.swagger.codegen.CodegenOperation;
import com.wordnik.swagger.codegen.CodegenResponse;
import com.wordnik.swagger.codegen.SupportingFile;
import com.wordnik.swagger.codegen.languages.JaxRSServerCodegen;
import com.wordnik.swagger.models.Operation;
import com.wordnik.swagger.models.Response;
import com.wordnik.swagger.models.properties.ArrayProperty;
import com.wordnik.swagger.models.properties.MapProperty;
import com.wordnik.swagger.models.properties.Property;

public class WatsonAppJaxRSServerCodegen extends JaxRSServerCodegen {

    private static final String CONFIG_FILE_CLASSPATH_NAME = "watson-app-codegen.properties";
    private static final String CONFIG_FILE_SYS_PROP = "com.ibm.watson.app.common.codegen.config";

    @Override
    public String getName() {
        return "watson-app-jaxrs";
    }

    @Override
    public String getHelp() {
        return "Generates a Java JAXRS Server application for use within an IBM Watson Gallery application.";
    }

    public WatsonAppJaxRSServerCodegen() {
        super();
        final Properties config = loadConfiguration();
        
        if(config.isEmpty()) {
            System.err.println("WARNING: No configuration supplied - using defaults");
        }

        // Override values defined in JaxRSServerCodegen
        invokerPackage = config.getProperty("invokerPackage", "com.ibm.watson.app.common.rest.api");
        groupId = config.getProperty("groupId", "com.ibm.watson.app");
        artifactId = config.getProperty("artifactId", "framework-ega-rest-api");
        artifactVersion = config.getProperty("artifactVersion", "1.0.0");
        // sourceFolder = config.getProperty("sourceFolder", "src/main/java"); // Default should be fine for this, no need to externalize
        title = config.getProperty("title", "Watson Gallery App Server Interface");

        // Override values defined in DefaultCodegen
        outputFolder = "generated-code" + File.separator + "watsonAppJavaJaxRS";
        templateDir = "WatsonAppJaxRS";
        apiPackage = config.getProperty("apiPackage", invokerPackage);
        modelPackage = config.getProperty("modelPackage", invokerPackage + ".model");

        additionalProperties.put("invokerPackage", invokerPackage);
        additionalProperties.put("groupId", groupId);
        additionalProperties.put("artifactId", artifactId);
        additionalProperties.put("artifactVersion", artifactVersion);
        additionalProperties.put("title", title);

        apiTemplateFiles.put("apiInterface.mustache", "Interface.java");

        supportingFiles.clear();
        supportingFiles.add(new SupportingFile("ApiException.mustache",
                (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "ApiException.java"));
        supportingFiles.add(new SupportingFile("ApiResponseMessage.mustache",
                (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "ApiResponseMessage.java"));
        supportingFiles.add(new SupportingFile("NotFoundException.mustache",
                (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "NotFoundException.java"));
        
        // The follow will generate Java Client classes for the API as well as the Jax-RS bindings 
        boolean enableClientGeneration = BooleanUtils.toBoolean(config.getProperty("enableClientGeneration", "false"));
        if ( enableClientGeneration ) {
            apiTemplateFiles.put("apiClient.mustache", "Client.java");
    
            supportingFiles.add(new SupportingFile("abstractClientApi.mustache",
                  (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "AbstractClientApi.java"));
        }

    }

    private Properties loadConfiguration() {
        final Properties config = new Properties();
        // First, try the system property
        final String configFilename = System.getProperty(CONFIG_FILE_SYS_PROP);
        if(configFilename != null) {
            try(InputStream is = new FileInputStream(new File(configFilename))) {
                config.load(is);
                System.out.println("Loaded configuration from: " + configFilename);
            } catch (IOException e) {
                System.err.println("Unable to read configuration file '" + configFilename + "', attempting to load from classpath");
            }
        }
        
        if(config.isEmpty()) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_CLASSPATH_NAME)) {
                if(is != null) {
                   config.load(is);
                }
            } catch (IOException e) {
                System.err.println("Unable to read configuration file '" + CONFIG_FILE_CLASSPATH_NAME + "' from classpath, using defaults");
            }
        }
        return config;
    }

    @Override
    public String apiFileFolder() {
        // The default implementation uses "/" in place of File.separatorChar, correct it here
        return outputFolder + File.separatorChar + sourceFolder + File.separatorChar + apiPackage().replace('.', File.separatorChar);
    }

    @Override
    public String modelFileFolder() {
        // The default implementation uses "/" in place of File.separatorChar, correct it here
        return outputFolder + File.separatorChar + sourceFolder + File.separatorChar + modelPackage().replace('.', File.separatorChar);
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        super.addOperationToGroup(tag, resourcePath, operation, co, operations);
        if (co.baseName.equals("/")) {
            co.baseName = "";
        }
    }
    
    
    public String sourceFolder() {
       return sourceFolder;
    }

    @Override
    public String getTypeDeclaration(Property p)
    {
        if (p instanceof MapProperty) {
            MapProperty mp = (MapProperty) p;
            Property inner = mp.getAdditionalProperties();

            return getSwaggerType(p) + "<String, " +getTypeDeclaration(inner) +">";
        }
        return super.getTypeDeclaration(p);
    }

    @Override
    public String toDefaultValue(Property p) {
        if (p instanceof MapProperty) {
            return "new HashMap<>()";
        }
        if (p instanceof ArrayProperty) {
            return "new ArrayList<>()";
        }

        return super.toDefaultValue(p);
    }

    @Override
    public CodegenResponse fromResponse( String responseCode, Response response ) {
        // make sure a container type of array is treated same as list
       final CodegenResponse r = super.fromResponse(responseCode, response);
       if ( r.containerType != null ) {
          r.isListContainer = Boolean.valueOf("array".equals(r.containerType));
       }
       return r;
    }

}
