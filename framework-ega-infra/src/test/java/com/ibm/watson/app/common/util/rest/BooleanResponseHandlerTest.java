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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ibm.watson.app.common.util.rest.BooleanResponseHandler;

@RunWith(MockitoJUnitRunner.class)
public class BooleanResponseHandlerTest {
	private final BooleanResponseHandlerTest GIVEN = this, WHEN = this, THEN = this, AND = this;
	
	@Mock
	protected HttpResponse httpResponse;
	
	BooleanResponseHandler handler;
	private Boolean result;
	
	@Test
	public void test_get_200_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_OK);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_successful();
	}
	
	@Test
	public void test_get_201_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_CREATED);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	@Test
	public void test_get_202_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_ACCEPTED);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	@Test
	public void test_get_300_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_MULTIPLE_CHOICES);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	@Test
	public void test_get_301_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_MOVED_PERMANENTLY);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	@Test
	public void test_get_404_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_NOT_FOUND);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	@Test
	public void test_get_500_response() throws Exception {
		GIVEN.response_handler_is_created();
			AND.http_response_returns(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		WHEN.handle_response_is_invoked();
		THEN.verify_return_is_not_successful();
	}
	
	private void verify_return_is_successful() {
		verify_return_is(true);
	}
	
	private void verify_return_is_not_successful() {
		verify_return_is(false);
	}
	
	private void verify_return_is(boolean expectedResult) {
		assertEquals(expectedResult, result);
	}
	
	private void http_response_returns(int code) {
		final StatusLine sl = mock(StatusLine.class);
		when(httpResponse.getStatusLine()).thenReturn(sl);
		when(sl.getStatusCode()).thenReturn(code);
	}
	
	private void response_handler_is_created() {
		handler = new BooleanResponseHandler();
	}

	private void handle_response_is_invoked() throws IOException {
		result = handler.handleResponse(httpResponse);
	}
}
