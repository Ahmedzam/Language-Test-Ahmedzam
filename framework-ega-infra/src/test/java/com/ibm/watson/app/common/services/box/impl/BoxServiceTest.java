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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.net.HttpHeaders;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.watson.app.common.services.box.BoxService;
import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse;
import com.ibm.watson.app.common.services.box.model.BoxMetadataResponse.Entry;
import com.ibm.watson.app.common.services.box.model.BoxSearchResponse;
import com.ibm.watson.app.common.services.box.model.BoxSearchResponse.FileInfo;

@RunWith(MockitoJUnitRunner.class)
public class BoxServiceTest {
	@SuppressWarnings("unused")
	private final BoxServiceTest GIVEN = this, WHEN = this, WITH = this, THEN = this, AND = this;

	private static final String DEFAULT_DEVELOPER_TOKEN = "abc123def456";
	private static final String TEMPLATE = "properties";
	private static final String SCOPE = "global";
	
	protected final Gson gson = new GsonBuilder()
	  .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
	  .create();

	@Mock protected CloseableHttpClient httpClient;
	@Mock protected HttpResponse httpResponse;
	@Mock protected HttpEntity httpEntity;

	protected BoxService boxService;
	protected BoxRestClient restClient;	
	protected String entityContent;
	
	protected BoxSearchResponse expectedSearchResponse, actualSearchResponse;
	protected BoxMetadataResponse expectedMetadataResponse, actualMetadataResponse;
	protected String expectedContentResponse, actualContentResponse;
	
	// SEARCH TESTS
	@Test
	public void test_search_1_result() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_search_response();
		  AND.a_new_search_result_is_added("1", "first", "tag1", "tag2");
		  AND.response_handler_returns(expectedSearchResponse);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(1);
		  AND.verify_search_result_contains("1", "first", "tag1", "tag2");
		  AND.verify_http_client_execute_invoked();
	}

	@Test
	public void test_search_empty_query() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_search_response();
		  AND.response_handler_returns(expectedSearchResponse);
		WHEN.box_search_is_invoked("");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(0);
		  AND.verify_http_client_execute_invoked();
	}

	@Test 
	public void test_search_more_than_1_result() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_search_response();
		  AND.a_new_search_result_is_added("1", "first", "tag1", "tag2");
		  AND.a_new_search_result_is_added("2", "second", "tag3", "tag4");
		  AND.response_handler_returns(expectedSearchResponse);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(2);
		  AND.verify_search_result_contains("1", "first", "tag1", "tag2");
		  AND.verify_search_result_contains("2", "second", "tag3", "tag4");
		  AND.verify_http_client_execute_invoked();
	}
	
	@Test 
	public void test_search_no_results() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_search_response();
		  AND.response_handler_returns(expectedSearchResponse);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(0);
		  AND.verify_http_client_execute_invoked();		
	}
	
	@Test 
	public void test_search_bad_request() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(400);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(-1);
		  AND.verify_http_client_execute_invoked();	
	}
	
	@Test
	public void test_search_server_error() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(500);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(-1);
		  AND.verify_http_client_execute_invoked();	
	}
	
	@Test
	public void test_search_unauthorized_error() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(401);
		WHEN.box_search_is_invoked("first");
		THEN.verify_search_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_search_result_count_is(-1);
		  AND.verify_http_client_execute_invoked();	
	}
	
	// METADATA TESTS
	@Test
	public void test_get_metadata_1_entry() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_metadata_response();
		  AND.a_new_metadata_result_is_added("myfirstclass", "what is your name?", TEMPLATE, SCOPE);
		  AND.response_handler_returns(expectedMetadataResponse);
		  AND.http_response_returns(200);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_contains(new Entry("myfirstclass", "what is your name?", TEMPLATE, SCOPE));
		  AND.verify_http_client_execute_invoked();
	}
	
	@Test
	public void test_get_metadata_2_entry() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_metadata_response();
		  AND.a_new_metadata_result_is_added("myfirstclass", "what is your name?", TEMPLATE, SCOPE);
		  AND.a_new_metadata_result_is_added("asecondclass", "what is your problem?", "foo", "enterprise");
		  AND.response_handler_returns(expectedMetadataResponse);
		  AND.http_response_returns(200);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_contains(new Entry("myfirstclass", "what is your name?", TEMPLATE, SCOPE));
		  AND.verify_http_client_execute_invoked();
	}
	
	@Test
	public void test_get_metadata_0_entry() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_metadata_response();
		  AND.response_handler_returns(expectedMetadataResponse);
		  AND.http_response_returns(200);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_is_empty();
		  AND.verify_http_client_execute_invoked();		
	}
	
	@Test
	public void test_get_metadata_not_found() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(404);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_is_empty();
		  AND.verify_http_client_execute_invoked();				
	}
	
	@Test
	public void test_get_metadata_unauthorized() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(401);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_is_empty();
		  AND.verify_http_client_execute_invoked();				
	}
	
	@Test
	public void test_get_metadata_bad_request() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(400);
		WHEN.box_metadata_is_invoked("12345");
		THEN.verify_metadata_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_metadata_result_is_empty();
		  AND.verify_http_client_execute_invoked();				
	}
		
	// CONTENT TESTS
    @Test
    public void test_get_content_found() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_content_response("<p>This is your answer</p>");
		  AND.response_handler_returns_string(expectedContentResponse);
		  AND.http_response_returns(200);
		WHEN.box_content_is_invoked("12345");
		THEN.verify_content_response_is_not_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_content_result_is(expectedContentResponse);
		  AND.verify_http_client_execute_invoked();				
    }
    
    @Test
    public void test_get_content_not_found() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.a_new_content_response("{\"type\":\"error\", \"status\":404,\"code\":\"not_found\"}");
		  AND.response_handler_returns_string(expectedContentResponse);
		  AND.http_response_returns(404);
		WHEN.box_content_is_invoked("12345");
		THEN.verify_content_response_is_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_http_client_execute_invoked();				
    }
    
    @Test
    public void test_get_content_unauthorized() throws Exception {
		GIVEN.mock_rest_client_is_created();
		  AND.box_service_is_created();
		GIVEN.http_response_returns(401);
		WHEN.box_content_is_invoked("12345");
		THEN.verify_content_response_is_null();
		  AND.verify_auth_header_is_set();
		  AND.verify_http_client_execute_invoked();    	
    }
    
	// HELPER METHODS
	protected void box_service_is_created() {
		boxService = new BoxServiceImpl(restClient);
	}

	protected void box_search_is_invoked(String query) {
		actualSearchResponse = boxService.search(query);
	}
	
	protected void box_metadata_is_invoked(String fileId) {
		actualMetadataResponse = boxService.getMetadata(fileId);
	}
	
	protected void box_content_is_invoked(String fileId) {
		actualContentResponse = boxService.getFileContents(fileId);
	}
	
	protected void a_new_search_response() {
		expectedSearchResponse = new BoxSearchResponse();
		expectedSearchResponse.setEntries(new ArrayList<FileInfo>());
	}

	protected void a_new_search_result_is_added(String id, String name, String... tags) {
		List<FileInfo> infos = expectedSearchResponse.getEntries();
		if( infos == null ) {
			infos = new ArrayList<FileInfo>();
		}
		infos.add( new FileInfo(id, name, tags));
		expectedSearchResponse.setEntries(infos);

		expectedSearchResponse.setTotalCount(infos.size());
	}
	
	protected void a_new_metadata_response() {
		expectedMetadataResponse = new BoxMetadataResponse();
		expectedMetadataResponse.setEntries(new ArrayList<Entry>());
	}
	
	protected void a_new_content_response(String content) {
		expectedContentResponse = content;
	}
	
	protected void a_new_metadata_result_is_added(String className, String canonicalQuestion, String template, String scope) {
		List<Entry> entries = expectedMetadataResponse.getEntries();
		if( entries == null ) {
			entries = new ArrayList<Entry>();
		}
		entries.add( new Entry(className, canonicalQuestion, template, scope));
		expectedMetadataResponse.setEntries(entries);
	}
	
	public void verify_search_result_count_is(int i) {
		assertEquals(i, actualSearchResponse.getTotalCount());
		if( i >= 0 ) { // no need to check if -1, that means a bad request
		    assertNotNull(actualSearchResponse.getEntries());
		    assertEquals(i, actualSearchResponse.getEntries().size());
		}
	}
	
	private void verify_search_result_contains(String id, String name, String... tags) {
		assertNotNull(actualSearchResponse);
		List<FileInfo> entries = actualSearchResponse.getEntries();
		assertNotNull(entries);
		
		// look for the specific entry passed in
		boolean found = false;
		for( FileInfo f : entries ) {
			if( f.getId().equals(id) ) {
				assertEquals(name, f.getName());
				assertEquals(tags[0], f.getTags()[0]);
				assertEquals(tags[1], f.getTags()[1]);	
				found = true;
			}
		}
		assertTrue(found);
	}

	private void verify_metadata_result_contains(Entry entry) {
		List<Entry> entries = actualMetadataResponse.getEntries();
		assertTrue(entries.size() > 0);
		
		// find it, there could be > 1 result
		Entry result = null;
		for( Entry e : entries ) {
			if( e.getClassName().equals(entry.getClassName()) 
					&& e.getScope().equals(entry.getScope()) 
					&& e.getTemplate().equals(entry.getTemplate())) {
				
				result = e;
			}
		}
		assertNotNull(result);
		
		// make sure we have a canonical question
	    assertNotNull(result.getCanonicalQuestion());
	    assertFalse(result.getCanonicalQuestion().isEmpty());
	}
	
	private void verify_metadata_result_is_empty() {
		List<Entry> entries = actualMetadataResponse.getEntries();
		assertEquals(0, entries.size());
	}
	
	private void verify_content_result_is(String result) {
		assertTrue(result.equals(actualContentResponse));
	}
	
	private void verify_search_response_is_not_null() {
		assertNotNull(actualSearchResponse);
	}
	
	private void verify_metadata_response_is_not_null() {
		assertNotNull(actualMetadataResponse);
	}
	
	private void verify_content_response_is_not_null() {
		assertNotNull(actualContentResponse);
	}
	
	private void verify_content_response_is_null() {
		assertNull(actualContentResponse);
	}
	
	@SuppressWarnings("unchecked")
	private void verify_auth_header_is_set() throws IOException {
		ArgumentCaptor<HttpUriRequest>cap = ArgumentCaptor.forClass(HttpUriRequest.class);
	    verify( httpClient, times(1) ).execute(cap.capture(), any(ResponseHandler.class), any(HttpContext.class));
	    assertTrue(cap.getValue() instanceof HttpGet);
	    Header[] headers = cap.getValue().getHeaders(HttpHeaders.AUTHORIZATION);
	    assertNotNull(headers);
	    assertEquals(1, headers.length);
	    assertEquals("Bearer " + DEFAULT_DEVELOPER_TOKEN, headers[0].getValue());
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void mock_rest_client_is_created() throws Exception {
		when(httpClient.execute(any(HttpUriRequest.class), any(ResponseHandler.class), any(HttpContext.class))).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation) throws Throwable {
						return ((ResponseHandler) invocation.getArguments()[1]).handleResponse(httpResponse);
					}
				});

		restClient = new BoxRestClient(DEFAULT_DEVELOPER_TOKEN, httpClient);
	}

	protected void response_handler_returns_string(String resp) throws Exception {
		http_entity_content_is(resp);
		http_response_returns(200);
	}
	
	// HELPER METHODS THAT COULD BE IN A COMMON TEST CLASS
	// (most of these were copied from NLClassifierText)
	protected void response_handler_returns(Object resp) throws Exception {
		http_entity_content_is(gson.toJson(resp));
		http_response_returns(200);
	}

	protected void http_entity_input_stream_throws(Exception exc) throws Exception {
		InputStream is = mock(InputStream.class, Mockito.withSettings().defaultAnswer(new ThrowsException(exc)));
		when(httpEntity.getContent()).thenReturn(is);
		when(httpEntity.getContentLength()).thenReturn(113l);
		add_json_content_type_header_to_http_entity();
		http_response_returns(200);
	}

	protected void http_entity_content_is(String content) throws Exception {
		when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
		when(httpEntity.getContentLength()).thenReturn((long) content.length());
		add_json_content_type_header_to_http_entity();
		http_response_returns(200);
	}

	protected void add_json_content_type_header_to_http_entity() throws Exception {
		Header ctHeader = mock(Header.class);
		HeaderElement ctHeaderElement = mock(HeaderElement.class);
		when(ctHeaderElement.getName()).thenReturn("application/json");
		when(ctHeader.getElements()).thenReturn(new HeaderElement[] {ctHeaderElement});
		when( ctHeaderElement.getParameters() ).thenReturn(new NameValuePair[]{});
		when(httpEntity.getContentType()).thenReturn(ctHeader);
	}

	protected void http_response_returns(int code) {
		final StatusLine sl = mock(StatusLine.class);
		when(httpResponse.getEntity()).thenReturn(httpEntity);
		when(httpResponse.getStatusLine()).thenReturn(sl);
		when(sl.getStatusCode()).thenReturn(code);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void verify_http_client_execute_invoked() throws Exception {
		ArgumentCaptor<HttpUriRequest> uriCap = ArgumentCaptor.forClass(HttpUriRequest.class);
		ArgumentCaptor<ResponseHandler> respCap = ArgumentCaptor.forClass(ResponseHandler.class);
		ArgumentCaptor<HttpContext> cxtCap = ArgumentCaptor.forClass(HttpContext.class);
		verify(httpClient, times(1)).execute(uriCap.capture(), respCap.capture(), cxtCap.capture());
	}

}
