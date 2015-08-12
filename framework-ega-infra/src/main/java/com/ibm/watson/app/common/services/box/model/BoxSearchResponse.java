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

import com.google.gson.annotations.Since;

public class BoxSearchResponse {
	
	public static class FileInfo {
		@Since(1.0)
		String id;
		
		@Since(1.0)
		String name;
		
		@Since(1.0)
		String[] tags;
		
		public FileInfo() {}
		
		public FileInfo(String id, String name, String... tags) {
			this.id = id;
			this.name = name;
			this.tags = tags;
		}
		
		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String[] getTags() {
			return tags;
		}

	}
  
	@Since(1.0)
	int totalCount;
	
	@Since(1.0)
	List<FileInfo> entries;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<FileInfo> getEntries() {
		return entries;
	}

	public void setEntries(List<FileInfo> entries) {
		this.entries = entries;
	}

}
