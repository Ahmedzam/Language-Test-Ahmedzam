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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Expose;


public class ClassifierTestAssertErrorData extends ClassifierTestData {

   @Expose
   int error;
   @Expose
   String message;
   
   @JsonProperty("error")
   public int getError() {
      return error;
   }

   @JsonSetter("error")
   public void setResponseCode( int responseCode ) {
      this.error = responseCode;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage( String responseMessage ) {
      this.message = responseMessage;
   }

   @Override 
   public Response getResponse() {
      return this.getErrorResponse(error, this);
   }
}
