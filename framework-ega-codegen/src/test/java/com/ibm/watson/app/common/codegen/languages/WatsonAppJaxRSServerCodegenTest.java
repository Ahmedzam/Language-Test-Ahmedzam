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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.ibm.watson.app.common.codegen.languages.WatsonAppJaxRSServerCodegen;
import com.wordnik.swagger.codegen.CodegenOperation;
import com.wordnik.swagger.models.Operation;

public class WatsonAppJaxRSServerCodegenTest {
    @SuppressWarnings("unused")
    private final WatsonAppJaxRSServerCodegenTest GIVEN = this, WHEN = this, WITH = this, THEN = this, AND = this;
    
    private static final String PACKAGE_PREFIX = "watson.codegen.package.prefix";
    
    private static final char dirSepChar = File.separatorChar;
    
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    
    String packagePrefix = "com.ibm.watson.app.common.rest.api";
    WatsonAppJaxRSServerCodegen codegen;
    CodegenOperation co;
    String fileFolder;
    
    
    
    @After
    public void cleanup() {
       Properties props = System.getProperties();
       props.remove(PACKAGE_PREFIX);
       System.setProperties(props);
    }
    
    @Test
    public void test_codegen_initilaize() {
       WHEN.codegen_is_created();
       THEN.verify_codegen_is_not_null();
       AND.verify_api_package_is(packagePrefix);
          AND.verify_model_package_is(packagePrefix + ".model");
          AND.verify_template_dir_is("WatsonAppJaxRS");
          AND.verify_output_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS");
    }

    
    @Test
    public void test_codegen_initilaize_prefix_override() throws Exception {
       GIVEN.package_prefix_set("pers.smp.test");
       WHEN.codegen_is_created();
       THEN.verify_codegen_is_not_null();
       AND.verify_api_package_is(packagePrefix + ".api");
          AND.verify_model_package_is(packagePrefix + ".api.model");
          AND.verify_template_dir_is("WatsonAppJaxRS");
          AND.verify_output_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS");
    }

    @Test
    public void test_api_file_folder() {
       GIVEN.codegen_is_created();
       WHEN.codegen_api_file_folder_is_invoked();
       THEN.verify_file_folder_is_not_null();
          AND.verify_file_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS", packagePrefix);
    }
    
    @Test
    public void test_api_file_folder_prefix_override() throws Exception  {
       GIVEN.package_prefix_set("pers.smp.test.api");
       AND.codegen_is_created();
       WHEN.codegen_api_file_folder_is_invoked();
       THEN.verify_file_folder_is_not_null();
          AND.verify_file_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS", packagePrefix);
    }
    

    @Test
    public void test_model_file_folder() {
       GIVEN.codegen_is_created();
       WHEN.codegen_model_file_folder_is_invoked();
       THEN.verify_file_folder_is_not_null();
          AND.verify_file_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS", packagePrefix + ".model");
    }

    @Test
    public void test_model_file_folder_prefix_override()  throws Exception {
       GIVEN.package_prefix_set("pers.smp.test.api");
       AND.codegen_is_created();
       WHEN.codegen_model_file_folder_is_invoked();
       THEN.verify_file_folder_is_not_null();
          AND.verify_file_folder_is("generated-code"+dirSepChar+"watsonAppJavaJaxRS", packagePrefix + ".model");
          
    }

    @Test
    public void test_add_operation_to_group() {
       GIVEN.codegen_is_created();
       WHEN.add_operation_invoked("tag1", "/resource/path", null, "/some/op/path");
       THEN.verify_codegen_operation_basepath_is("resource");
    }

    @Test
    public void test_add_operation_to_group_base_same_as_op() {
       GIVEN.codegen_is_created();
       WHEN.add_operation_invoked("tag1", "/resource/path", null, "/resource/path");
       THEN.verify_codegen_operation_basepath_is("resource");
    }

    @Test
    public void test_add_operation_to_group_empty_base() {
       GIVEN.codegen_is_created();
       WHEN.add_operation_invoked("tag1", "/", null, "/" );
       THEN.verify_codegen_operation_basepath_is("");
    }
    
    private void verify_file_folder_is_not_null() {
       assertNotNull(fileFolder);
    }
    
    private void verify_codegen_operation_basepath_is( String baseName ) {
       assertEquals( baseName , co.baseName );
    }

    private void add_operation_invoked( String tag, String resourcePath, Operation op, String codegenPath ) {
       
       co = new CodegenOperation();
       co.path=codegenPath;
       codegen.addOperationToGroup(tag, resourcePath, op, co, mock(Map.class));
   }

    private void verify_file_folder_is( String outputFolder, String packageName) {
       assertEquals( outputFolder + dirSepChar + codegen.sourceFolder() + dirSepChar + packageName.replace('.', dirSepChar), fileFolder );
    }

    private void verify_output_folder_is( String string ) {
       assertEquals( string, codegen.outputFolder() );
    }

    private void verify_template_dir_is( String string ) {
       assertEquals( string, codegen.templateDir() );
    }

    private void verify_model_package_is( String string ) {
       assertEquals( string, codegen.modelPackage() );
    }

    private void verify_api_package_is( String string ) {
       assertEquals( string, codegen.apiPackage() );
    }

    private void verify_codegen_is_not_null() {
       assertNotNull( codegen );
    }

    private void codegen_model_file_folder_is_invoked() {
       fileFolder = codegen.modelFileFolder();
    }
    
    private void codegen_api_file_folder_is_invoked() {
       fileFolder = codegen.apiFileFolder();
    }

    private void package_prefix_set( String string ) throws IOException {
       //System.setProperty(PACKAGE_PREFIX,  string );
        
        File propFile = folder.newFile();
        OutputStream out = new FileOutputStream(propFile);
        IOUtils.copy(this.getClass().getResourceAsStream("/test-watson-codegen.properties"), out);
        out.close();
        System.setProperty("com.ibm.watson.app.common.codegen.config", propFile.getAbsolutePath());
        packagePrefix = string;
    }

    private void codegen_is_created() {
      codegen = new WatsonAppJaxRSServerCodegen();
    }

}
