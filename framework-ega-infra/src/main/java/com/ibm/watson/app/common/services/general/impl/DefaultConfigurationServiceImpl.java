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

package com.ibm.watson.app.common.services.general.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ibm.watson.app.common.services.general.ConfigurationService;

/**
 * This service impl will first read a properties file as specific as a System parameter
 *    wim.config.service.properties
 * If that is not found or not specified it will read /properties/config_service.properties from the classpath
 * 
 * Lastly it will read the VCAP_SERVICES json object from the env.
 *   This will override any values from the above properties, so this always takes precedence  
 *  
 * @author spritko
 *
 */
public class DefaultConfigurationServiceImpl implements ConfigurationService {
   // @formatter:off
   private static final Logger logger = LogManager.getLogger();
	// @formatter:on

   Map<String, String> properties = new HashMap<>();

   public DefaultConfigurationServiceImpl() {
      // First read properties to get all defaults
      String props = System.getProperty("wim.config.service.properties");
      InputStream is = null;
      if ( props != null ) {
         try {
            is = new FileInputStream(props);
         } catch ( IOException e ) {
         }
      } else {
         is = getClass().getResourceAsStream("/properties/config_service.properties");
      }

      if ( is != null ) {
         Properties p = new Properties();
         try {
            p.load(is);
            for ( Object k : p.keySet() ) {
               properties.put((String) k, p.getProperty((String) k));
            }
         } catch ( IOException e ) {

         } finally {
            try {
               is.close();
            } catch ( IOException e ) {
            }
         }
      }
      // Read VCAP_SERVICES and override properties
      final String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
      if ( VCAP_SERVICES != null ) {
         if ( logger.isDebugEnabled() )
            logger.debug("VCAP_SERVICES is : " + VCAP_SERVICES);
         JsonParser parser = new JsonParser();
         JsonElement e = parser.parse(VCAP_SERVICES);
         JsonElement sqle = e.getAsJsonObject().get("sqldb");
         if ( sqle != null ) {
            sqle = sqle.getAsJsonArray().get(0);
            properties.put("db.name", sqle.getAsJsonObject().get("name").getAsString());
            properties.put("db.type", "sqldb");
         }
         JsonElement nlce = e.getAsJsonObject().get("natural_language_classifier.dev");
         if ( nlce != null ) {
            nlce = nlce.getAsJsonArray().get(0);
            nlce = nlce.getAsJsonObject().get("credentials");
            properties.put("nlclassifier.url", nlce.getAsJsonObject().get("url").getAsString());
            properties.put("nlclassifier.username", nlce.getAsJsonObject().get("username").getAsString());
            properties.put("nlclassifier.password", nlce.getAsJsonObject().get("password").getAsString());
         }
      }

      if ( logger.isDebugEnabled() )
         logger.debug("Resolved the following Properites " + properties);
   }

   @Override
   public String getProperty( String property, String defaultValue ) {
      if ( logger.isTraceEnabled() )
         logger.trace("getProperty() >>  property:" + property + "  defaultValue:" + defaultValue);
      String val = null;

      val = properties.get(property);
      if ( val == null )
         val = defaultValue;
      if ( logger.isTraceEnabled() )
         logger.trace("getProperty() << " + val);
      return val;
   }

   @Override
   public String getProperty( String property ) {
      return getProperty(property, null);
   }

}
