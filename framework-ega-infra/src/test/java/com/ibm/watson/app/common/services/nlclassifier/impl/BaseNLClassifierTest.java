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

package com.ibm.watson.app.common.services.nlclassifier.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierRestClient;

public class BaseNLClassifierTest {
	@SuppressWarnings("unused")
	private final BaseNLClassifierTest GIVEN = this, WHEN = this, WITH = this, THEN = this, AND = this;
	
	private static final String DEFAULT_URL = "http://www.example.com";
	
	protected final Gson gson = new GsonBuilder()
	.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
	.create();

	@Mock protected CloseableHttpClient httpClient;
	@Mock protected HttpEntity httpEntity;
	@Mock protected HttpResponse httpResponse;

	protected NLClassifierRestClient restClient;	
	protected String entityContent;
	
	protected void classifier_status_is(String id, String url, String status, String desc) throws Exception {
		// Can't use NLClassifierStatusResponse since we wouldn't be testing our status string => Status ENUM deserialization
		JsonObject obj = new JsonObject();
		obj.addProperty("classifier_id", id);
		obj.addProperty("url", url);
		obj.addProperty("status", status);
		obj.addProperty("status_description", desc);
		response_handler_returns(obj);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void verify_http_client_execute_invoked(String method, String contentType, String urlSuffix) throws Exception {
		ArgumentCaptor<HttpUriRequest> uriCap = ArgumentCaptor.forClass(HttpUriRequest.class);
		ArgumentCaptor<ResponseHandler> respCap = ArgumentCaptor.forClass(ResponseHandler.class);
		ArgumentCaptor<HttpContext> cxtCap = ArgumentCaptor.forClass(HttpContext.class);

		verify(httpClient, times(1)).execute(uriCap.capture(), respCap.capture(), cxtCap.capture());

		assertNotNull(uriCap.getValue());
		if(method.equalsIgnoreCase("post")) {
			assertTrue(uriCap.getValue().getClass().isAssignableFrom(HttpPost.class));
			HttpPost post = (HttpPost) uriCap.getValue();

			HttpEntity e = post.getEntity();
			assertNotNull(e);
			assertEquals(contentType, e.getContentType().getValue());
			assertTrue(e.getContentLength() > 0);

			entityContent = EntityUtils.toString(e, StandardCharsets.UTF_8);
			assertNotNull(entityContent);
			assertFalse(entityContent.isEmpty());
		} else if(method.equalsIgnoreCase("get")) {
			assertTrue(uriCap.getValue().getClass().isAssignableFrom(HttpGet.class));
		} else if(method.equalsIgnoreCase("delete")) {
			assertTrue(uriCap.getValue().getClass().isAssignableFrom(HttpDelete.class));
		}
		assertEquals(DEFAULT_URL + "/v1/classifiers" + urlSuffix, uriCap.getValue().getURI().toString());

		assertNotNull(respCap.getValue());
		assertNotNull(cxtCap.getValue());
	}
	
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void mock_rest_client_is_created() throws Exception {
		when(httpClient.execute(any(HttpUriRequest.class), any(ResponseHandler.class), any(HttpContext.class))).thenAnswer(
				new Answer<Object>() {
					@Override
					public Object answer(InvocationOnMock invocation) throws Throwable {
						return ((ResponseHandler) invocation.getArguments()[1]).handleResponse(httpResponse);
					}
				});

		restClient = new NLClassifierRestClient(DEFAULT_URL, httpClient);
	}
}
