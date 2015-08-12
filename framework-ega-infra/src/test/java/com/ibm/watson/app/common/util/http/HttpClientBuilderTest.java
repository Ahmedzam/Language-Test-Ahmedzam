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


package com.ibm.watson.app.common.util.http;

import static org.junit.Assert.assertNotNull;

import org.apache.http.auth.Credentials;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.watson.app.common.util.http.HttpClientBuilder;



public class HttpClientBuilderTest {

   @SuppressWarnings("unused")
   private final HttpClientBuilderTest GIVEN = this, WHEN = this, WITH = this, THEN = this, AND = this;

   CloseableHttpClient client;
   
   
   @Test
   public void test_default_client() {
      WHEN.http_client_is_built(null);
      THEN.verify_http_client_is_not_null();
   }

   
   @Test
   public void test_default_client_with_credentials() {
      WHEN.http_client_is_built(Mockito.mock(Credentials.class));
      THEN.verify_http_client_is_not_null();
   }

   private void verify_http_client_is_not_null() {
      assertNotNull(client);
   }


   private void http_client_is_built( Credentials creds ) {
      client = HttpClientBuilder.buildDefaultHttpClient(creds);
   }

}
