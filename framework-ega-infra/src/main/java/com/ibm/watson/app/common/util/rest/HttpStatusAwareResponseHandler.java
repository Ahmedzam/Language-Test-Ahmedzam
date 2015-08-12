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
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ibm.watson.app.common.util.rest.MessageKey;

/**
 * This response handler is aware of the HTTP status code returned in the response.
 * It accepts the response as valid iff the {@link #acceptStatusCode(int)} method returns {@code true}. 
 * Otherwise, a default return value will be returned (defined as {@code null}, subclasses may override).
 * By default, this handler only considers the response codes in the range {@code 200 - 207} as valid.
 */
public abstract class HttpStatusAwareResponseHandler<T> implements ResponseHandler<T> {
	private static final Logger logger = LogManager.getLogger();
	
	private final int[] validStatusCodes = new int[] {
			HttpStatus.SC_OK,
			HttpStatus.SC_CREATED,
		    HttpStatus.SC_ACCEPTED,
		    HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION,
		    HttpStatus.SC_NO_CONTENT,
		    HttpStatus.SC_RESET_CONTENT,
		    HttpStatus.SC_PARTIAL_CONTENT,
		    HttpStatus.SC_MULTI_STATUS
	};

	/* (non-Javadoc)
	 * @see org.apache.http.client.ResponseHandler#handleResponse(org.apache.http.HttpResponse)
	 */
	@Override
	public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();
		if(logger.isDebugEnabled()) {
			logger.debug("Endpoint response status code " + status);
		}
		
		if(!acceptStatusCode(status)) {
			logger.warn(MessageKey.AQWEGA02001W_received_invalid_http_status_2.getMessage(status, EnglishReasonPhraseCatalog.INSTANCE.getReason(status, Locale.ENGLISH)));
			return getDefaultReturnValue();
		}
		
		return doHandleResponse(response);
	}

	/**
	 * Check if the HTTP response status code returned from the request is valid.
	 * By default, only response codes in the range {@code 200 - 207} are valid.
	 * @param status The HTTP status code
	 * @return boolean true if the supplied status constitutes a valid response, false otherwise
	 */
	protected boolean acceptStatusCode(int status) {
		for(int validStatus : validStatusCodes) {
			if(validStatus == status) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the default return value that should be returned if an error occurs handling the response.
	 * By default, this method returns {@code null}.  
	 * @return T The default return value
	 */
	public T getDefaultReturnValue() {
		return null;
	}
	
	/**
	 * Handle the response. This method is invoked after we have considered it valid.
	 * @param response
	 * @return T The value to return from the response
	 * @throws IOException if an IOException
	 */
	protected T doHandleResponse(HttpResponse response) throws IOException {
		final HttpEntity entity = response.getEntity();
		try {
			return handleEntity(entity);
		} catch(IOException e) {
			// By contract, let the implementation handle this exception
			throw e;
		} catch(Exception e) {
			logger.error(MessageKey.AQWEGA04000E_error_while_handling_resoinse_entity_1.getMessage(e.getMessage()));
			logger.catching(e);
			return getDefaultReturnValue();
		} finally {
			EntityUtils.consume(entity);
		}
	}
	
	/**
	 * Handle the response entity. There is no need to close / consume the entity, this will be done automatically.
	 * Any exceptions thrown by this method will be caught and the default return value will be used.
	 * @param entity The response entity
	 * @return T The value to return from the response
	 * @throws Exception If any exception occurs during the handling of the response entity
	 */
	protected T handleEntity(HttpEntity entity) throws Exception {
		return getDefaultReturnValue();
	}
}
