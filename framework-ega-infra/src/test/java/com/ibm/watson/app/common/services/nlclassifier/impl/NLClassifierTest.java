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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier.Status;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse.NLClassifiedClass;

@RunWith(MockitoJUnitRunner.class)
public class NLClassifierTest extends BaseNLClassifierTest {
	@SuppressWarnings("unused")
	private final NLClassifierTest GIVEN = this, WHEN = this, THEN = this, WITH = this, AND = this;
	
	private NLClassifierImpl classifier;
	
	private NLClassiferClassifyResponse expectedClassifyResponse, actualClassifyResponse;
	
	private Status classifierStatus;
	private boolean classifierDeleted;
	private String classifierId;

	@Test
	public void test_classify_one_classified_class() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.175);
			AND.response_handler_returns(expectedClassifyResponse);
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
			AND.verify_classified_items_size_is(1);
			AND.verify_classified_items_item_is(0, "class1", 0.175);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_multiple_classified_classes() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.a_class_is_added_to_the_classify_response("class2", 0.875);
			AND.a_class_is_added_to_the_classify_response("class3", 0.775);
			AND.a_class_is_added_to_the_classify_response("class4", 0.675);
			AND.a_class_is_added_to_the_classify_response("class5", 0.575);
			AND.response_handler_returns(expectedClassifyResponse);
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
			AND.verify_classified_items_size_is(5);
			AND.verify_classified_items_item_is(0, "class1", 0.975);
			AND.verify_classified_items_item_is(1, "class2", 0.875);
			AND.verify_classified_items_item_is(2, "class3", 0.775);
			AND.verify_classified_items_item_is(3, "class4", 0.675);
			AND.verify_classified_items_item_is(4, "class5", 0.575);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_gets_bad_response() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.response_handler_returns(expectedClassifyResponse);
			AND.http_response_returns(400);
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
		    AND.verify_classified_items_size_is(0);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_gets_io_expection() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.response_handler_returns(expectedClassifyResponse);
			AND.http_entity_input_stream_throws(new IOException("Cannot read"));
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
	        AND.verify_classified_items_size_is(0);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_gets_npe() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.response_handler_returns(expectedClassifyResponse);
			AND.http_entity_input_stream_throws(new NullPointerException("Cannot read"));
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
            AND.verify_classified_items_size_is(0);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_gets_non_json_response() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.response_handler_returns("this text is not json");
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
            AND.verify_classified_items_size_is(0);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_classify_gets_valid_json_response_but_invalid_schema() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
		GIVEN.a_new_classify_response();
			AND.a_class_is_added_to_the_classify_response("class1", 0.975);
			AND.http_entity_content_is("{\"thisIsThe\":\"wrong format\",\"for\":\"the data\"}");
			AND.http_response_returns(200);
		WHEN.classifier_classify_is_invoked("Some text to classify");
		THEN.verify_classified_items_is_not_null();
            AND.verify_classified_items_size_is(0);
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "/testId12345/classify");
			AND.verify_entity_content_is_classify_data();
	}
	
	@Test
	public void test_get_status_training() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "Training", "the classifier is currently training");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.TRAINING);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_non_existent() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "Non Existent", "the classifier does not exist");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.NON_EXISTENT);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_failed() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "Failed", "the classifier failed");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.FAILED);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_available() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "Available", "the classifier is available");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.AVAILABLE);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_unavailable() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "Unavailable", "the classifier is unavailable");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNAVAILABLE);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_unknown() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "¯\\(°_o)/¯", "i dunno what the classifier is up to");
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_valid_but_gets_500() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "training", "the classifier is training");
			AND.http_response_returns(500);
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_valid_but_response_throws_npe() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "training", "the classifier is training");
			AND.http_entity_input_stream_throws(new NullPointerException("Cannot read"));
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_valid_but_response_throws_io_exception() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.classifier_status_is("testId12345", "some_url", "training", "the classifier is training");
			AND.http_entity_input_stream_throws(new IOException("Cannot read"));
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_response_is_not_json() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("this is not json");
			AND.http_response_returns(200);
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_get_status_response_has_invalid_schema() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("{\"thisIsThe\":\"wrong format\",\"for\":\"the data\"}");
			AND.http_response_returns(200);
		WHEN.classifier_get_status_is_invoked();
		THEN.verify_classifier_status_not_null();
			AND.verify_classifier_state_is(Status.UNKNOWN);
			AND.verify_http_client_execute_invoked("get", null, "/testId12345");
	}
	
	@Test
	public void test_delete() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("");
			AND.http_response_returns(200);
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_deleted();
	}
	
	@Test
	public void test_delete_json_response_should_be_ignored() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("{}");
			AND.http_response_returns(200);
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_deleted();
	}
	
	@Test
	public void test_delete_non_json_response_should_be_ignored() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("this is not json");
			AND.http_response_returns(200);
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_deleted();
	}

	@Test
	public void test_delete_gets_500() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("");
			AND.http_response_returns(500);
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_not_deleted();
	}
	
	@Test
	public void test_delete_gets_response_throws_npe() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("");
			AND.http_response_returns(200);
			AND.http_entity_input_stream_throws(new NullPointerException("Cannot read"));
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_deleted();
	}
	
	@Test
	public void test_delete_gets_response_throws_io_exception() throws Exception {
		GIVEN.classifier_is_created();
			AND.mock_rest_client_is_created();
			AND.rest_client_is_set_in_classifier();
			AND.http_entity_content_is("");
			AND.http_response_returns(200);
			AND.http_entity_input_stream_throws(new IOException("Cannot read"));
		WHEN.classifier_delete_is_invoked();
		THEN.verify_http_client_execute_invoked("delete", null, "/testId12345");
			AND.classifier_was_deleted();
	}
	
	@Test
	public void test_get_id() throws Exception {
		GIVEN.classifier_is_created();
		WHEN.classifier_get_id_is_invoked();
		THEN.verify_classifier_id_is_not_null();
	}
	
	private void verify_classifier_id_is_not_null() {
		assertNotNull(classifierId);
	}

	private void classifier_was_not_deleted() {
		assertFalse(classifierDeleted);
	}
	
	private void classifier_was_deleted() {
		assertTrue(classifierDeleted);
	}

	private void verify_classifier_state_is(Status status) {
		assertEquals(status, classifierStatus);
	}
	
	private void verify_classifier_status_not_null() {
		assertNotNull(classifierStatus);
	}
	
	private void verify_entity_content_is_classify_data() {
		JsonParser parser = new JsonParser();
		
		JsonElement json = parser.parse(entityContent);
		assertTrue(json.isJsonObject());
		
		JsonObject jsonObject = json.getAsJsonObject();
		assertTrue(jsonObject.has("text"));
		
		String text = jsonObject.get("text").getAsString();
		assertNotNull(text);
		assertEquals(expectedClassifyResponse.getText(), text);
	}

	private void verify_classified_items_item_is(int index, String text, double conf) {
		NLClassifiedClass classifiedClass = actualClassifyResponse.getClasses().get(index);
		assertNotNull(classifiedClass);
		assertEquals(text, classifiedClass.getClassName());
		assertEquals(conf, classifiedClass.getConfidence(), 0);
	}

	private void verify_classified_items_size_is(int i) {
		List<NLClassifiedClass> classes = actualClassifyResponse.getClasses();
		assertNotNull(classes);
		assertEquals(i, classes.size());
	}
	
	private void verify_classified_items_is_not_null() {
		assertNotNull(actualClassifyResponse);
	}

	private void a_class_is_added_to_the_classify_response(String className, double confidence) {
		List<NLClassifiedClass> classes = expectedClassifyResponse.getClasses();
		if(classes.isEmpty()) {
			expectedClassifyResponse.setTopClass(className);
		}
		classes.add(new NLClassifiedClass(className, confidence));
	}
	
	private void a_new_classify_response() {
         expectedClassifyResponse = new NLClassiferClassifyResponse();
         expectedClassifyResponse.setClasses(new ArrayList<NLClassifiedClass>());
	}
	
	private void classifier_get_status_is_invoked() {
		classifierStatus = classifier.getStatus();
	}
	
	private void classifier_get_id_is_invoked() {
		classifierId = classifier.getId();
	}
	
	private void classifier_classify_is_invoked(String text) {
		expectedClassifyResponse.setText(text);
		actualClassifyResponse = classifier.classify(text);
	}
	
	private void classifier_delete_is_invoked() {
		classifierDeleted = classifier.delete();
	}

	private void rest_client_is_set_in_classifier() {
		classifier.setRestClient(restClient);
	}
	
	private void classifier_is_created() {
		classifier = new NLClassifierImpl("testId12345");
	}
}
