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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * This response handler checks the HTTP status code to determine if a valid response has been received.
 * It returns {@code true} if the HTTP status code is valid, {@code false} otherwise.
 * By default, only {@code 200 OK} is considered valid.
 * It ignores the response entity. 
 */
public class BooleanResponseHandler extends HttpStatusAwareResponseHandler<Boolean> {
	@Override
	protected boolean acceptStatusCode(int status) {
		// Only 200 OK is valid by default
		return status == HttpStatus.SC_OK;
	}
	
	@Override
	public Boolean getDefaultReturnValue() {
		// By default, under any error, we return false
		return Boolean.FALSE;
	}
	
	@Override
	protected Boolean doHandleResponse(HttpResponse response) throws IOException {
		// If we get here, we accepted the response as valid, so return true
		return Boolean.TRUE;
	}
}
