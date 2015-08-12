/*
 * Copyright IBM Corp. 2015
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.common.util.rest;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.LocalizedMessage;



public class WatsonMessage extends LocalizedMessage {

   private static final Logger logger = LogManager.getLogger();

   final String key;
   
   public WatsonMessage(ResourceBundle bundle, Locale locale, String key, Object arg1, Object arg2) {
      super(bundle, locale, key, arg1, arg2);
      this.key = key;
   }

   public WatsonMessage(ResourceBundle bundle, Locale locale, String key, Object arg) {
      super(bundle, locale, key, arg);
      this.key = key;
   }

   public WatsonMessage(ResourceBundle bundle, Locale locale, String key, Object[] arguments) {
      super(bundle, locale, key, arguments);
      this.key = key;
   }

   public WatsonMessage(ResourceBundle bundle, String key, Object arg1, Object arg2) {
      super(bundle, key, arg1, arg2);
      this.key = key;
   }

   public WatsonMessage(ResourceBundle bundle, String key, Object arg) {
      super(bundle, key, arg);
      this.key = key;
   }

   public WatsonMessage(ResourceBundle bundle, String key, Object[] arguments) {
      super(bundle, key, arguments);
      this.key = key;
   }


   @Override
   public String getFormattedMessage() {
      
      String msg = super.getFormattedMessage();
      
      if ( msg.equals(key) ) {
         logger.error("ERROR: ** MISSING MessageKey " + key + " from bundle " );
      }
      
      if ( key.startsWith("AQW") ) {
         int idx = key.indexOf("_");
         msg = key.substring(0, idx) + " - " + msg;
      }
      
      return msg;
   }
   
}

