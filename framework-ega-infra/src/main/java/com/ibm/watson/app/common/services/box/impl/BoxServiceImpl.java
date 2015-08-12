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

package com.ibm.watson.app.common.services.box.impl;

import com.ibm.watson.app.common.services.box.BoxService;
import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse;
import com.ibm.watson.app.common.services.box.model.BoxSearchResponse;

public class BoxServiceImpl implements BoxService {
    private BoxRestClient client;
    
	public BoxServiceImpl() {
		initialize();
	}

	public BoxServiceImpl(BoxRestClient client) {
		this();
		this.client = client;
	}
	
	public void initialize() {
		client = new BoxRestClient(System.getenv().get("BOX_DEVELOPER_TOKEN"));
	}

	@Override
	public String getFileContents(String fileId) {
		return client.getContent(fileId);
	}

	@Override
	public BoxSearchResponse search(String query) {
		return client.search(query);
	}

	@Override
	public BoxMetadataResponse getMetadata(String fileId) {
		return client.getMetadata(fileId);
	}

}
