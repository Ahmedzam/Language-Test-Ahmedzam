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

import java.nio.charset.Charset;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.Charsets;

@Plugin(name = "TagEventLayout", category = "Core", elementType = "layout", printObject = true)
public class TagEventLayout extends AbstractStringLayout {


   @PluginFactory
   public static TagEventLayout createLayout(
         @PluginAttribute(value="format", defaultString="json") String format,
         @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset ) {
      return new TagEventLayout( format, charset);
   }

   public static TagEventLayout createDefaultLayout() {
      return new TagEventLayout( "json", Charsets.UTF_8);
   }

   
   boolean generateCsv = false;
   String[]messageFormat;
   public TagEventLayout(String format, Charset charset) {
      super(charset);
      messageFormat = new String[] {TagEventMessage.FORMAT_JSON};
      generateCsv = format.equalsIgnoreCase("csv");
      if ( generateCsv ) {
         messageFormat = new String[] {TagEventMessage.FORMAT_CSV};
      }
   }
   
   @Override
   public String toSerializable( LogEvent logEvent ) {
      if ( logEvent.getMarker() != null && logEvent.getMarker().isInstanceOf(TagEventMarkers.TAG_EVENT_MARKER) ) {
         if ( logEvent.getMessage() instanceof TagEventMessage ) {
            return ((TagEventMessage)logEvent.getMessage()).getFormattedMessage(messageFormat);
         }
      }
      return "";
   }
   
}
