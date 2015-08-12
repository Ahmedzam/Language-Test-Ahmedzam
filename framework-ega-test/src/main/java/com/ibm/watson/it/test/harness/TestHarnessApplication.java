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

package com.ibm.watson.it.test.harness;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.fasterxml.jackson.jaxrs.cfg.JaxRSFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;


@ApplicationPath("/testharness")
public class TestHarnessApplication extends javax.ws.rs.core.Application {

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>>classes = new HashSet<>();
      classes.add( ClassifierTestService.class );
      return classes;
   }
   


   @Override
   public Set<Object> getSingletons() {

       Set<Object> s = new HashSet<Object>();
       
       // Register the Jackson provider for JSON
       
       JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();
       jaxbProvider.enable(JaxRSFeature.ALLOW_EMPTY_INPUT);
       s.add(jaxbProvider);
       
       return s;
   }
   
}
