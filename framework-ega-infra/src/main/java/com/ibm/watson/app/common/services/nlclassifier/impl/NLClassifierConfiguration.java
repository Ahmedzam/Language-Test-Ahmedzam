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

package com.ibm.watson.app.common.services.nlclassifier.impl;

import com.google.gson.annotations.Since;
import com.ibm.watson.app.common.services.bluemix.BaseBluemixServiceConfiguration;

public class NLClassifierConfiguration extends BaseBluemixServiceConfiguration {
	@Since(1.0) private NLClassifierCredentials credentials;

	public NLClassifierCredentials getCredentials() {
		return credentials;
	}
	
	public static class NLClassifierCredentials {
		@Since(1.0) private String url;
		@Since(1.0) private String username;
		@Since(1.0) private String password;
		
		public String getUrl() {
			return url;
		}
		public String getUsername() {
			return username;
		}
		public String getPassword() {
			return password;
		}
	}
}
