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

package com.ibm.watson.app.common.services.box.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

public class BoxMetadataResponse {
	
	public static class Entry {
		@Since(1.0)
		@SerializedName("className")
		String className;
		
		@Since(1.0)
		@SerializedName("canonicalQuestion")
		String canonicalQuestion;
		
		@Since(1.0)
		@SerializedName("$template")
		String template;
		
		@Since(1.0)
		@SerializedName("$scope")
		String scope;
		
		public Entry() {}
		
		public Entry(String className, String canonicalQuestion, String template, String scope) {
			this.className = className;
			this.canonicalQuestion = canonicalQuestion;
			this.template = template;
			this.scope = scope;
		}
		
		public String getClassName() {
			return className;
		}

		public String getCanonicalQuestion() {
			return canonicalQuestion;
		}

		@JsonProperty("$template")
		public String getTemplate() {
			return template;
		}

		@JsonSetter("$template")
		public void setTemplate(String template) {
			this.template = template;
		}

		@JsonProperty("$scope")
		public String getScope() {
			return scope;
		}

		@JsonSetter("$scope")
		public void setScope(String scope) {
			this.scope = scope;
		}
	}
	
	@Since(1.0)
	List<Entry> entries;

	@Since(1.0)
	int limit;

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public int getLimit() {
		return limit;
	}

}
