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

package com.ibm.watson.it.test.harness.model;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

public abstract class ClassifierTestData {

   @Expose String text;
   
   @JsonIgnore
   public abstract Response getResponse();

   
   public String getText() {
      return text;
   }
   public void setText( String text ) {
      this.text = text;
   }

   Response getOkResponse( Object entity ) {
      final ResponseBuilder builder = Response.ok()
            .header("Pragma", "no-cache")
            .header("Cache-Control", "no-cache");
      if ( entity != null ) {
         builder.entity(entity);
      }
      return builder.build();
   }

   Response getErrorResponse( int code, Object entity ) {
      
      final ResponseBuilder builder = Response.status(Response.Status.fromStatusCode(code))
            .header("Pragma", "no-cache")
            .header("Cache-Control", "no-cache");
      if ( entity != null ) {
         builder.entity(entity);
      }
      return builder.build();
   }

}
