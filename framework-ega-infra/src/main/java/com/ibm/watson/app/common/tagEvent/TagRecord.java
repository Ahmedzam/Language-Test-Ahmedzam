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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public abstract class TagRecord implements Iterable<String> {

   static final protected String dateFormatString = "yyyyMMddHHmmssZ";
   
   static final Gson gson = new GsonBuilder()
      .disableHtmlEscaping()
      .setDateFormat(dateFormatString)
      .excludeFieldsWithoutExposeAnnotation()
      .setVersion(1.0)
      .create();

   static protected ThreadLocal<DateFormat> dateFormatter = new ThreadLocal<DateFormat>() {

      @Override
      protected DateFormat initialValue() {
         return new SimpleDateFormat(dateFormatString);
      }
      
   };
   
   protected String type; 
   
   
   public String getType() {
      return type;
   }


   public void setType( String type ) {
      this.type = type;
   }

   abstract protected void prepareForFormat();

   public String asJson( ) {
      prepareForFormat();
      JsonObject json = new JsonObject();
      json.add(type, gson.toJsonTree(this));
      return gson.toJson(json);
   }
   
   public String asCsv() {
      prepareForFormat();
      StringBuilder sb = new StringBuilder();
      try {
         CSVPrinter printer = new CSVPrinter(sb, CSVFormat.DEFAULT);
         printer.printRecord( this );
         printer.close();
      } catch ( IOException e) {
         
      }
      return sb.toString();
      
   }
   
}
