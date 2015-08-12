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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifierService;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierImpl;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierServiceImpl;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData.TrainingInstance;

@RunWith(MockitoJUnitRunner.class)
public class NLClassifierServiceTest extends BaseNLClassifierTest {
	@SuppressWarnings("unused")
	private final NLClassifierServiceTest GIVEN = this, WHEN = this, THEN = this, WITH = this, AND = this;

	private NLClassifierService service;

	private List<NLClassifier> classifiers;
	private NLClassifierTrainingData trainingData;
	private NLClassifier classifier;
	
	@Test
	public void test_get_classifiers_no_ids() throws Exception {
		GIVEN.response_classifier_ids_are();
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_one_id() throws Exception {
		GIVEN.response_classifier_ids_are("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(1);
			AND.verify_classifiers_list_contains("nl12345");
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_multiple_ids() throws Exception {
		GIVEN.response_classifier_ids_are("nl12345", "nl23456", "nl34567");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(3);
			AND.verify_classifiers_list_contains("nl12345");
			AND.verify_classifiers_list_contains("nl23456");
			AND.verify_classifiers_list_contains("nl34567");
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_500_response() throws Exception {
		GIVEN.response_classifier_ids_are("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_response_returns(500);
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_response_causes_npe() throws Exception {
		GIVEN.response_classifier_ids_are("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new NullPointerException("Can't read"));
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_response_causes_io_exception() throws Exception {
		GIVEN.response_classifier_ids_are("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new IOException("Can't read"));
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_response_is_not_json() throws Exception {
		GIVEN.http_entity_content_is("this is not json");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_response_is_valid_json_but_invalid_schema() throws Exception {
		GIVEN.http_entity_content_is("{ \"classifiers\": \"abc,123\"}" );
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifiers_response_is_valid_json_but_invalid_schema_within_classifier_data() throws Exception {
		GIVEN.http_entity_content_is("{\"classifiers\":[{\"im not including\":\"classifier id field here\"}]}");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifiers_is_invoked();
		THEN.verify_classifiers_list_is_not_null();
			AND.verify_classifiers_list_size_is(0);
			AND.verify_http_client_execute_invoked("get", null, "");
	}
	
	@Test
	public void test_get_classifier_no_id() throws Exception {
		GIVEN.response_classifier_id_is("");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifier_is_invoked("");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/");
	}
	
	@Test
	public void test_get_classifier() throws Exception {
		GIVEN.response_classifier_id_is("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_not_null();
			AND.verify_classifier_id_is("nl12345");
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	@Test
	public void test_get_classifier_gets_500() throws Exception {
		GIVEN.response_classifier_id_is("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_response_returns(500);
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	@Test
	public void test_get_classifier_gets_response_throws_io_exception() throws Exception {
		GIVEN.response_classifier_id_is("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new IOException("Can't read"));
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	@Test
	public void test_get_classifier_gets_response_throws_npe() throws Exception {
		GIVEN.response_classifier_id_is("nl12345");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new NullPointerException("Can't read"));
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	@Test
	public void test_get_classifier_gets_response_not_json() throws Exception {
		GIVEN.http_entity_content_is("this is not json");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	@Test
	public void test_get_classifier_response_valid_json_invalid_schema() throws Exception {
		GIVEN.http_entity_content_is("{\"classifier\":\"wrong schema\"}");
			AND.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.get_classifier_is_invoked("nl12345");
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("get", null, "/nl12345");
	}
	
	// Ignoring all of the test_train until we implement a new version of the create/train call
	// the create/train methods are not currently used in the app
	@Ignore
	@Test
	public void test_train_classifier() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_not_null();
			AND.verify_classifier_id_is("nlc1234-23");
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_multiple_training_instances() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.training_instance_is_added("text2", "class2", "class2.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_not_null();
			AND.verify_classifier_id_is("nlc1234-23");
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_gets_500() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_response_returns(500);
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_response_causes_io_exception() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new IOException("Can't read"));
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_response_causes_npe() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_input_stream_throws(new NullPointerException("Can't read"));
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_response_is_not_json() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_content_is("this is not json");
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}
	
	@Ignore
	@Test
	public void test_train_classifier_response_is_valid_json_but_invalid_schema() throws Exception {
		GIVEN.training_data_is_created();
			AND.training_instance_is_added("text1", "class1", "class1.2");
			AND.classifier_status_is("nlc1234-23", "some_url", "training", "the classifier is training");
		GIVEN.mock_rest_client_is_created();
			AND.classifier_service_is_created();
			AND.http_entity_content_is("{\"classifier\":\"wrong schema\"}");
		WHEN.create_classifier_is_invoked();
		THEN.verify_classifier_is_null();
			AND.verify_http_client_execute_invoked("post", "application/json; charset=UTF-8", "");
	}

	private void verify_classifier_id_is(String id) {
		assertEquals(id, classifier.getId());
	}
	
	private void verify_classifier_is_not_null() {
		assertNotNull(classifier);
	}

	private void verify_classifier_is_null() {
		assertNull(classifier);
	}

	private void verify_classifiers_list_contains(String expectedId) {
		for(NLClassifier c : classifiers) {
			if(c.getId().equals(expectedId)) {
				return;
			}
		}
		fail("Classifier id " + expectedId + " not found in returned classifiers : " + classifiers);
	}

	private void verify_classifiers_list_size_is(int i) {
		assertEquals(i, classifiers.size());
	}

	private void verify_classifiers_list_is_not_null() {
		assertNotNull(classifiers);
	}
	
	private void response_classifier_ids_are(String... ids) throws Exception {
		List<NLClassifier> classifiers = new ArrayList<>();
		for(String id : ids) {
			classifiers.add(new NLClassifierImpl(id));
		}

		Type typeOfSrc = new TypeToken<List<NLClassifier>>() {}.getType();

		JsonObject response = new JsonObject();
		response.add("classifiers", gson.toJsonTree(classifiers, typeOfSrc));
		http_entity_content_is(gson.toJson(response));
	}
	
	private void response_classifier_id_is(String id) throws Exception {
		JsonObject json = new JsonObject();
		json.addProperty("classifier_id", id);
		json.addProperty("url", "some_url");
		response_handler_returns(json);
	}
	
	private void training_instance_is_added(String text, String ... classes) {
		trainingData.getTrainingData().add(new TrainingInstance(text, classes));
	}
	
	private void training_data_is_created() {
		trainingData = new NLClassifierTrainingData();
		trainingData.setTrainingData(new ArrayList<TrainingInstance>());
	}

	private void get_classifier_is_invoked(String classifierId) {
		classifier = service.getClassifier(classifierId);
	}
	
	private void create_classifier_is_invoked() {
		classifier = service.createClassifier(trainingData);
	}
	
	private void get_classifiers_is_invoked() {
		classifiers = service.getClassifiers();
	}
	
	private void classifier_service_is_created() {
		service = new NLClassifierServiceImpl(restClient);
	}
}
