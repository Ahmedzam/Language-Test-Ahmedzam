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

package com.ibm.watson.app.common.services.sqldb.impl;

import com.google.gson.annotations.Since;
import com.ibm.watson.app.common.services.bluemix.BaseBluemixServiceConfiguration;

public class SqlDBConfiguration extends BaseBluemixServiceConfiguration {
	@Since(1.0) private SqlDBCredentials credentials;
	
	public SqlDBCredentials getCredentials() {
		return credentials;
	}

	public static class SqlDBCredentials {
		@Since(1.0) private int port;
		@Since(1.0) private String db;
		@Since(1.0) private String username;
		@Since(1.0) private String host;
		@Since(1.0) private String hostname;
		@Since(1.0) private String jdbcurl;
		@Since(1.0) private String uri;
		@Since(1.0) private String password;
		
		public int getPort() {
			return port;
		}
		public String getDb() {
			return db;
		}
		public String getUsername() {
			return username;
		}
		public String getHost() {
			return host;
		}
		public String getHostname() {
			return hostname;
		}
		public String getJdbcurl() {
			return jdbcurl;
		}
		public String getUri() {
			return uri;
		}
		public String getPassword() {
			return password;
		}
	}
}
