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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.net.HttpHeaders;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse;
import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse.Entry;
import com.ibm.watson.app.common.services.box.model.BoxSearchResponse;
import com.ibm.watson.app.common.util.rest.JSONResponseHandler;
import com.ibm.watson.app.common.util.rest.MessageKey;
import com.ibm.watson.app.common.util.rest.SimpleRestClient;
import com.ibm.watson.app.common.util.rest.StringResponseHandler;

public class BoxRestClient extends SimpleRestClient {
	private static final Logger logger = LogManager.getLogger();

	private static final String BASE_URL = "https://api.box.com/2.0";
	private static final String SEARCH_ENDPOINT = "/search?query=%22${query_string}%22&content_types=name&fields=name";
    private static final String DOWNLOAD_ENDPOINT = "/files/${file_id}/content";
    private static final String METADATA_ENDPOINT = "/files/${file_id}/metadata";

	private final Gson gsonUnderscores = new GsonBuilder()
		.setVersion(1.0)
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();

	private final Gson gson = new GsonBuilder()
	    .setVersion(1.0)
	    .create();
	
	protected final String accessToken;
	
    /**
     * Creates the REST client to call Box API using the access token passed in
     * 
     * @param token - access token used to make the API calls
     */
	public BoxRestClient(String accessToken) {
		// we create our own client to pass in because there is no super() constructor
		// that will take the access token or just a url
		super(BASE_URL, HttpClients.createDefault());
		this.accessToken = accessToken;
	}
	
	public BoxRestClient(String accessToken, CloseableHttpClient client) {
		super(BASE_URL, client);
		this.accessToken = accessToken;
	}
	
	/**
	 * We need to add the access token as an auth header to every call
	 */
	@Override
	protected <T> T doGet(HttpGet httpget, ResponseHandler<? extends T> responseHandler) throws IOException {
		httpget.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));
		return super.doGet(httpget, responseHandler);
	}

	/**
	 * Response handler for handling response from the search API call
	 */
	private final JSONResponseHandler<BoxSearchResponse> searchResponseHandler = new JSONResponseHandler<BoxSearchResponse>(gsonUnderscores, BoxSearchResponse.class) {
		@Override
		protected void validate(BoxSearchResponse object) throws InvalidObjectException {
		}
		
		// this method is only here for debug
		@Override
		protected boolean acceptStatusCode(int status) {
			if( logger.isDebugEnabled() ) {
			    logger.debug("HTTP Status code for search request: " + status);
			}
			return super.acceptStatusCode(status);
		}

		// this method is here only for debug
		@Override
		protected BoxSearchResponse parseJSON(String jsonString) throws JsonSyntaxException {
			if( logger.isDebugEnabled() ) {
			    logger.debug("Search JSON response: " + jsonString);
			}
			return super.parseJSON(jsonString);
		}

		@Override
		public BoxSearchResponse getDefaultReturnValue() {
			BoxSearchResponse response = new BoxSearchResponse();
			response.setTotalCount(-1);
			return response;
		}
	};

	/**
	 * Calls the Box API to search for a document and return the results
	 * 
	 * @param query - the query string to search for
	 * @return
	 */
	public BoxSearchResponse search(String query) {
		try {
			String endpoint = getSearchEndpoint(query);
			if( logger.isDebugEnabled()) {
			    logger.debug("Search endpoint: " + endpoint);
			}
			return get(endpoint, searchResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14200E_error_box_search_request_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return searchResponseHandler.getDefaultReturnValue();
	}

	/**
	 * Calls the Box API to retrieve the contents of a file
	 * 
	 * @param fileId - id of the file to retrieve the content for
	 * @return
	 */
	public String getContent(String fileId) {
		try {
			String endpoint = getFileIdEndpoint(DOWNLOAD_ENDPOINT, fileId);
			if( logger.isDebugEnabled() ) {
			  logger.debug("Download endpoint: " + endpoint);
			}
			return get(endpoint, new StringResponseHandler(StandardCharsets.UTF_8));
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14202E_error_box_content_request_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return "";
	}
	
	/**
	 * Response handler for handling response from the search API call
	 */
	private final JSONResponseHandler<BoxMetadataResponse> metadataResponseHandler = new JSONResponseHandler<BoxMetadataResponse>(gson, BoxMetadataResponse.class) {
		@Override
		protected void validate(BoxMetadataResponse object) throws InvalidObjectException {
		}
		
		// this is here for debug purposes only
		@Override
		protected boolean acceptStatusCode(int status) {
			if( logger.isDebugEnabled() ) {
				logger.debug("HTTP Status code for metadata request: " + status);
			}
			return super.acceptStatusCode(status);
		}
		
		// this is here for debug purposes only
		@Override
		protected BoxMetadataResponse parseJSON(String jsonString) throws JsonSyntaxException {
			if( logger.isDebugEnabled() ) {
			   logger.debug("Metadata JSON Response: " + jsonString);
			}
			return super.parseJSON(jsonString);
		}

		@Override
		public BoxMetadataResponse getDefaultReturnValue() {
			BoxMetadataResponse response = new BoxMetadataResponse();
			response.setEntries(new ArrayList<Entry>());
			return response;
		}
	};

	public BoxMetadataResponse getMetadata(String fileId) {
		try {
			String endpoint = getFileIdEndpoint(METADATA_ENDPOINT, fileId);
			if( logger.isDebugEnabled() ) {
			     logger.debug("Metadata endpoint: " + endpoint);
			}
			return get(endpoint, metadataResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14201E_error_box_metadata_request_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return metadataResponseHandler.getDefaultReturnValue();
	}
	
	private String getSearchEndpoint( String query) {
		return SEARCH_ENDPOINT.replace("${query_string}", query);
	}

	private String getFileIdEndpoint(String endpoint, String fileId) {
		return endpoint.replace("${file_id}", fileId);
	}
}
