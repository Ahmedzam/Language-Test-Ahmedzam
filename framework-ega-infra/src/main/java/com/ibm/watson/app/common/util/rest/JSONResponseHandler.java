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
import java.io.InvalidObjectException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.app.common.util.rest.MessageKey;

/**
 * This response handler deserializes the HTTP entity as JSON.
 * 
 * @param <T> The type representing the JSON data
 */
public abstract class JSONResponseHandler<T> extends HttpStatusAwareResponseHandler<T> {
	private static final Logger logger = LogManager.getLogger();
	
	private final Gson gson;
	private static final Charset CHARSET = StandardCharsets.UTF_8; // JSON should always be UTF-8
	
	protected Class<T> classOfT;
	public JSONResponseHandler(Class<T> classOfT) {
		this(new Gson(), classOfT);
	}
	
	public JSONResponseHandler(Gson gson, Class<T> classOfT) {
		this.gson = gson;
		this.classOfT = classOfT;
	}

	@Override
	protected T handleEntity(HttpEntity entity) throws IOException {
		final String jsonString = EntityUtils.toString(entity, CHARSET);
		if(logger.isTraceEnabled()) {
			logger.trace("Response JSON: " + jsonString);
		}
		
		if(jsonString == null || jsonString.isEmpty()) {
			if( logger.isDebugEnabled()) {
				logger.debug("Received null or empty JSON string '" + jsonString + "'");
			}
			return getDefaultReturnValue();
		}
		
		try {
			T retval = parseJSON(jsonString);
			validate(retval);
			return retval;
		} catch(JsonSyntaxException e) {
			logger.error(MessageKey.AQWEGA04001E_unable_parse_json_1.getMessage(e.getMessage()));
			logger.catching(e);
			return getDefaultReturnValue();
		}
	}

	/**
	 * Parse the supplied string as JSON. 
	 * This method will never receive a {@code null} or empty string.
	 * @param jsonString The JSON string
	 * @return T An object representing the deserialized JSON 
	 * @throws JsonSyntaxException
	 */
	protected T parseJSON(String jsonString) throws JsonSyntaxException {
		return gson.fromJson(jsonString, classOfT);
	}
	
	/**
	 * Check if the object returned from deserialization is valid.
	 * This method will never receive {@code null}.
	 * @param T An object representing the deserialized JSON 
	 * @throws InvalidObjectException if the supplied object is not valid
	 */
	protected abstract void validate(T object) throws InvalidObjectException;
}
