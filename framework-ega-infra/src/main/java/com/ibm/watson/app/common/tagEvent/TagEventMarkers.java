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

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;


public class TagEventMarkers {

   public  static final Marker TAG_EVENT_MARKER = MarkerManager.getMarker("tagEvent");
   public  static final Marker ARTCLE_CLICK_MARKER = MarkerManager.getMarker("tagEvent_articleClick").setParents(TAG_EVENT_MARKER);
   public  static final Marker CALL_AGENT_MARKER = MarkerManager.getMarker("tagEvent_agent").setParents(TAG_EVENT_MARKER);


}
