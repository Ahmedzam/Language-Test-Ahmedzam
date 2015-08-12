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
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

/**
 * This response handler converts the response entity into a String.
 */
public class StringResponseHandler extends HttpStatusAwareResponseHandler<String> {
	private final Charset charset;
	
	/**
	 * Construct a {@link StringResponseHandler} with no charset. 
	 * It is recommended to explicitly specify the charset with {@link #StringResponseHandler(Charset)} 
	 * instead of allowing this response handler to use the default charset (ISO--8859-1)
	 */
	public StringResponseHandler() {
		this(null);
	}
	
	/**
	 * Construct a {@link StringResponseHandler} that will convert the response entity to a string with the supplied Charset.
	 */
	public StringResponseHandler(Charset charset) {
		this.charset = charset;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.watson.app.common.rest.HttpStatusAwareResponseHandler#handleEntity(org.apache.http.HttpEntity)
	 */
	@Override
	protected String handleEntity(HttpEntity entity) throws IOException {
		return EntityUtils.toString(entity, charset);
	}
}
