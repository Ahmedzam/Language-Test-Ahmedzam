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
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;


public class ClassifierTestAssertData extends ClassifierTestData {
   
   @Expose NLClassiferClassifyResponse  response;
   
   @JsonProperty("response")
   public NLClassiferClassifyResponse getClassifierResponse() {
      return response;
   }
   @JsonSetter("response")
   public void setResponse( NLClassiferClassifyResponse response ) {
      this.response = response;
   }
   @Override
   public String toString() {
      return "ClassifierTestAssertData [text=" + text + ", response=" + response + "]";
   }
   
   @Override
   public Response getResponse() {
      return getOkResponse(response);
   }
   
}
