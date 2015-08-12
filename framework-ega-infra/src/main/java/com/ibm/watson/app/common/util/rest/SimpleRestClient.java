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

package com.ibm.watson.app.common.util.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.app.common.util.http.HttpClientBuilder;

/**
 * A simple thread-safe REST client
 */
public abstract class SimpleRestClient {
	private static final Logger logger = LogManager.getLogger();

	protected final String url;
	protected final CloseableHttpClient httpClient;

	public static final ResponseHandler<String> DEFAULT_STRING_RESPONSE_HANDLER = new StringResponseHandler(StandardCharsets.UTF_8);

	public SimpleRestClient(String url) {
		this(url, HttpClients.createDefault());
	}

	public SimpleRestClient(String url, CloseableHttpClient client) {
		this.url = url;
		this.httpClient = client;
	}

	public SimpleRestClient(String url, String username, String password) {
		this.url = url;

		httpClient = HttpClientBuilder.buildDefaultHttpClient(new UsernamePasswordCredentials(username, password));
	}

	// GET
	protected String get(String endpoint) throws IOException {
		return get(endpoint, DEFAULT_STRING_RESPONSE_HANDLER);
	}

	protected <T> T get(String endpoint, ResponseHandler<? extends T> responseHandler) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Making GET request at endpoint '" + endpoint + "'");
		}

		HttpGet httpget = new HttpGet(url + endpoint);
		return doGet(httpget, responseHandler);
	}

	protected <T> T doGet(HttpGet httpget, ResponseHandler<? extends T> responseHandler) throws IOException {
		// Subclasses can override for additional functionality
		return execute(httpget, responseHandler);
	}

	// POST

	protected String post(String endpoint, HttpEntity entity) throws IOException {
		return post(endpoint, entity, DEFAULT_STRING_RESPONSE_HANDLER);
	}

	protected <T> T post(String endpoint, HttpEntity entity, ResponseHandler<? extends T> responseHandler) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Making POST request at endpoint '" + endpoint + "'");
			if(logger.isTraceEnabled()) {
				logger.trace("POST request body:\n" + EntityUtils.toString(entity));
			}
		}

		HttpPost httppost = new HttpPost(url + endpoint);
		if(entity != null) {
			httppost.setEntity(entity);
		}
		return doPost(httppost, responseHandler);
	}

	protected <T> T doPost(HttpPost httppost, ResponseHandler<? extends T> responseHandler) throws IOException {
		// Subclasses can override for additional functionality
		return execute(httppost, responseHandler);
	}

	// DELETE

	protected String delete(String endpoint) throws IOException {
		return delete(endpoint, DEFAULT_STRING_RESPONSE_HANDLER);
	}

	protected <T> T delete(String endpoint, ResponseHandler<? extends T> responseHandler) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Making DELETE request at endpoint '" + endpoint + "'");
		}

		HttpDelete httpdelete = new HttpDelete(url + endpoint);
		return doDelete(httpdelete, responseHandler);
	}

	protected <T> T doDelete(HttpDelete httpdelete, ResponseHandler<? extends T> responseHandler) throws IOException {
		// Subclasses can override for additional functionality
		return execute(httpdelete, responseHandler);
	}
	

	   
	public <T> T put( String endpoint, HttpEntity entity, ResponseHandler<? extends T> responseHandler) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Making PUT request at endpoint '" + endpoint + "'");
			if(logger.isTraceEnabled()) {
				logger.trace("PUT request body:\n" + EntityUtils.toString(entity));
			}
		}

		HttpPut httppost = new HttpPut(url + endpoint);
		if(entity != null) {
			httppost.setEntity(entity);
		}
		return doPut(httppost, responseHandler);
	}

	protected <T> T doPut(HttpPut httppost, ResponseHandler<? extends T> responseHandler) throws IOException {
		// Subclasses can override for additional functionality
		return execute(httppost, responseHandler);
	}


	protected <T> T execute(HttpRequestBase request, ResponseHandler<? extends T> responseHandler) throws IOException {
		return httpClient.execute(request, responseHandler, HttpClientContext.create());
	}
}
