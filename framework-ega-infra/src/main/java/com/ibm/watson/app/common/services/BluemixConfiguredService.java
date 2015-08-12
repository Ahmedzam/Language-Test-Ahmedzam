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

package com.ibm.watson.app.common.services;

import java.util.List;

/**
 * A service bound to this application from Bluemix
 */
public interface BluemixConfiguredService extends ConfiguredService {
	/**
	 * Get the name of this service
	 * @return String The service name
	 */
	public String getName();

	/**
	 * Get the label declared by this service
	 * @return String The label
	 */
	public String getLabel();
	
	/**
	 * Get the tags declared by this service
	 * @return The list of tags
	 */
	public List<String> getTags();
	
	/**
	 * Get the plan of this service
	 * @return String The plan name
	 */
	public String getPlan();
}
