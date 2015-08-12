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

import java.util.Arrays;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ibm.watson.app.common.util.rest.MessageKey;


public class HttpClientBuilder {

   private static final Logger logger = LogManager.getLogger();

   public HttpClientBuilder() {
      // TODO Auto-generated constructor stub
   }

   
   public static CloseableHttpClient buildDefaultHttpClient(Credentials cred ) {
      
      // Use custom cookie store if necessary.
      CookieStore cookieStore = new BasicCookieStore();
      // Use custom credentials provider if necessary.
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
      RequestConfig defaultRequestConfig;
      
     //private DefaultHttpClient client;
     connManager.setMaxTotal(200);
     connManager.setDefaultMaxPerRoute(50);
     try {
         SSLContextBuilder builder = new SSLContextBuilder();
         builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        
          // Create a registry of custom connection socket factories for supported
          // protocol schemes.
          Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
              .register("http", PlainConnectionSocketFactory.INSTANCE)
              .register("https", new SSLConnectionSocketFactory(builder.build()))
              .build();
     } catch (Exception e ) {
        logger.warn(MessageKey.AQWEGA02000W_unable_init_ssl_context.getMessage(), e );
     }
       // Create global request configuration
       defaultRequestConfig = RequestConfig.custom()
           .setCookieSpec(CookieSpecs.BEST_MATCH)
           .setExpectContinueEnabled(true)
           .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC, AuthSchemes.NTLM, AuthSchemes.DIGEST))
           .setAuthenticationEnabled(true)
           .build();

       if ( cred != null )
          credentialsProvider.setCredentials(AuthScope.ANY,  cred );
      
       return HttpClients.custom()
             .setConnectionManager(connManager)
             .setDefaultCookieStore(cookieStore)
             .setDefaultCredentialsProvider(credentialsProvider)
             .setDefaultRequestConfig(defaultRequestConfig)
             .build();
  }
  

}
