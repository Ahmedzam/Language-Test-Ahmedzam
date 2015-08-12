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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.runners.MockitoJUnitRunner;

import com.ibm.watson.app.common.util.rest.JSONResponseHandler;

@RunWith(MockitoJUnitRunner.class)
public class JSONResponseHandlerTest {
	private final JSONResponseHandlerTest GIVEN = this, WHEN = this, THEN = this, AND = this;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	protected HttpEntity httpEntity;
	
	@Mock
	protected HttpResponse httpResponse;
	
	@Spy
	private JSONResponseHandler<MyEntity> handler = new JSONResponseHandler<MyEntity>(MyEntity.class){
		@Override
		protected void validate(MyEntity object) throws InvalidObjectException {
			if( object.key == null && object.iKey == null && object.bKey == null) {
				throw new InvalidObjectException("All of the keys in the returned entity were null");
			}
		}
	};
	
	private MyEntity entity;

	@Test
	public void test_get_200_response() throws Exception {
		GIVEN.http_entity_content_is("{\"key\":\"keyVal\",\"iKey\":123,\"bKey\":true}");
		WHEN.extract_entity_is_invoked();
		THEN.verify_entity_is_not_null();
			AND.verify_validate_was_invoked();
			AND.entity_key_is("keyVal");
			AND.entity_int_key_is(123);
			AND.entity_boolean_key_is(true);
	}

	@Test
	public void test_extract_entity_some_entries() throws Exception {
		GIVEN.http_entity_content_is("{\"key\":\"keyVal\",\"bKey\":true}");
		WHEN.extract_entity_is_invoked();
		THEN.verify_entity_is_not_null();
			AND.verify_validate_was_invoked();
			AND.entity_key_is("keyVal");
			AND.entity_int_key_is(null);
			AND.entity_boolean_key_is(true);
	}

	@Test
	public void test_extract_entity_extra_entries() throws Exception {
		GIVEN.http_entity_content_is("{\"k\":\"keyVal\",\"bK\":true}");
		thrown.expect(InvalidObjectException.class);
		WHEN.extract_entity_is_invoked();
	}

	@Test
	public void test_extract_entity_diff_schema() throws Exception {
		GIVEN.http_entity_content_is("{\"key\":\"keyVal\",\"bKey\":true,\"aKey\":true,\"dKey\":3.14}");
		WHEN.extract_entity_is_invoked();
		THEN.verify_entity_is_not_null();
			AND.verify_validate_was_invoked();
			AND.entity_key_is("keyVal");
			AND.entity_int_key_is(null);
			AND.entity_boolean_key_is(true);
	}

	@Test
	public void test_extract_entity_not_json() throws Exception {
		GIVEN.http_entity_content_is("ok");
		WHEN.extract_entity_is_invoked();
		THEN.verify_entity_is_null();
	}

	@Test
	public void test_handle_response() throws Exception {
		GIVEN.http_entity_content_is("{\"key\":\"keyVal\",\"iKey\":123,\"bKey\":true}");
			AND.http_response_returns(200);
		WHEN.handle_response_is_invoked();
		THEN.verify_entity_is_not_null();
			AND.entity_key_is("keyVal");
			AND.entity_int_key_is(123);
			AND.entity_boolean_key_is(true);
	}

	@Test
	public void test_handle_response_status_is_400() throws Exception {
		GIVEN.http_entity_content_is("{\"key\":\"keyVal\",\"iKey\":123,\"bKey\":true}");
			AND.http_response_returns(400);
		WHEN.handle_response_is_invoked();
		THEN.verify_entity_is_null();
	}

	@Test
	public void test_handle_response_non_json_entity() throws Exception {
		GIVEN.http_entity_content_is("ok");
			AND.http_response_returns(200);
		WHEN.handle_response_is_invoked();
		THEN.verify_entity_is_null();
	}

	@Test
	public void test_handle_response_extract_throws_IOException() throws Exception {
		GIVEN.http_entity_input_stream_throws(new IOException("cannot read"));
			AND.http_response_returns(200);
		thrown.expect(IOException.class);
		WHEN.handle_response_is_invoked();
	}
	
	private void verify_validate_was_invoked() throws InvalidObjectException {
		verify(handler, times(1)).validate(any(MyEntity.class));
	}
	
	private void entity_boolean_key_is(Boolean b) {
		assertEquals(b, entity.bKey);
	}
	
	private void entity_int_key_is(Integer i) {
		assertEquals(i, entity.iKey);
	}
	
	private void entity_key_is(String string) {
		assertEquals(string, entity.key);
	}

	private void verify_entity_is_null() {
		assertNull(entity);
	}

	private void verify_entity_is_not_null() {
		assertNotNull(entity);
	}

	private void http_entity_input_stream_throws(Exception exc) throws Exception {
		InputStream is = mock(InputStream.class, Mockito.withSettings().defaultAnswer(new ThrowsException(exc)));
		when(httpEntity.getContent()).thenReturn(is);
		when(httpEntity.getContentLength()).thenReturn(113l);
		add_default_header_to_http_entity();
	}

	private void http_entity_content_is(String content) throws Exception {
		when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes("UTF-8")));
		when(httpEntity.getContentLength()).thenReturn((long) content.length());
		add_default_header_to_http_entity();
	}
	
	private void add_default_header_to_http_entity() throws Exception {
		Header ctHeader = mock(Header.class);
		HeaderElement ctHeaderElement = mock(HeaderElement.class);
		when(ctHeaderElement.getName()).thenReturn("application/json");
		when(ctHeaderElement.getParameters()).thenReturn(new NameValuePair[] {});
		when(ctHeader.getElements()).thenReturn(new HeaderElement[] {ctHeaderElement});
		when(httpEntity.getContentType()).thenReturn(ctHeader);
	}
	
	private void http_response_returns(int code) {
		final StatusLine sl = mock(StatusLine.class);
		when(httpResponse.getStatusLine()).thenReturn(sl);
		when( httpResponse.getEntity() ).thenReturn(httpEntity);
		when(sl.getStatusCode()).thenReturn(code);
	}

	private void handle_response_is_invoked() throws Exception {
		entity = handler.handleResponse(httpResponse);
	}

	private void extract_entity_is_invoked() throws Exception {
		entity = handler.handleEntity(httpEntity);
	}

	static class MyEntity {
		String key;
		Integer iKey;
		Boolean bKey;
	}
}
