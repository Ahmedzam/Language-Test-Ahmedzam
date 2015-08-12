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

package com.ibm.watson.app.common.services.box;

import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse;
import com.ibm.watson.app.common.services.box.model.BoxSearchResponse;

public interface BoxService {
	/**
	 * Uses the Box API to retrieve the contents of the file with the associated Id
	 * 
	 * @param fileId - id of the file to retrieve the contents for
	 * @return - String representation of the contents of the file
	 */
  public String getFileContents(String fileId);
  
  /**
   * Uses the Box API to search for a particular file
   * 
   * @param query - query string to use to search for the file (will be quoted so is an exact search)
   * @return - response object with the search results
   */
  public BoxSearchResponse search(String query);
  
  /**
   * Uses the Box API to retrieve the metadata for the file with the associated Id
   * 
   * @param fileId - id of the file to retrieve the metadata for
   * @return - response object with all of the metadata for the file
   */
  public BoxMetadataResponse getMetadata(String fileId);
}
