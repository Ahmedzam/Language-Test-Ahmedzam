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

package com.ibm.watson.app.common.tagEvent;

import org.apache.logging.log4j.message.MultiformatMessage;
import org.apache.logging.log4j.message.ObjectMessage;


public class TagEventMessage extends ObjectMessage implements MultiformatMessage {
   
   static final String FORMAT_CSV  =  "text/csv";
   static final String FORMAT_JSON =  "application/json";
   

   static final String[] FORMATS = new String[] { FORMAT_JSON, FORMAT_CSV };
   
   TagRecord record;
   
   public TagEventMessage(TagRecord tag) {
      super(tag);
      record = tag;
   }
   
   @Override
   public String[] getFormats() {
      return FORMATS;
   }

   @Override
   public String getFormattedMessage( String[] formats ) {
      String format = FORMAT_JSON;
      if ( formats != null && formats.length>0) {
         for ( String f : formats ) {
            for ( String F : FORMATS) {
               if ( f.equalsIgnoreCase(F) ) {
                  format = f;
                  break;
               }
            }
         }
      }
      switch (format) {
         case FORMAT_CSV:
            return record.asCsv();
         case FORMAT_JSON:
         default:
            return record.asJson() + "\n";
      }
   }
   
   
}
