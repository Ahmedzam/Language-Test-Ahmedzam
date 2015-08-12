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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier.Status;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierConfiguration.NLClassifierCredentials;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse.NLClassifiedClass;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierClassifyRequest;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierStatusResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData;
import com.ibm.watson.app.common.util.rest.BooleanResponseHandler;
import com.ibm.watson.app.common.util.rest.JSONEntity;
import com.ibm.watson.app.common.util.rest.JSONResponseHandler;
import com.ibm.watson.app.common.util.rest.SimpleRestClient;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class NLClassifierRestClient extends SimpleRestClient {
	private static final Logger logger = LogManager.getLogger();
	
	public static final String CLASSIFIERS_ENDPOINT = "/v1/classifiers";
	public static final String CLASSIFIER_ENDPOINT = "/v1/classifiers/${classifier_id}";
	public static final String CLASSIFY_ENDPOINT = "/v1/classifiers/${classifier_id}/classify";
	
	private final JsonParser jsonParser = new JsonParser();
	private final Gson gson = new GsonBuilder()
		.setVersion(1.0)
		.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
		.create();
	
	private final JSONResponseHandler<List<NLClassifier>> classifiersResponseHandler = new NLClassifierJsonResponseHandler<List<NLClassifier>>(this) {		
		@Override
		protected List<NLClassifier> parseJSON(String jsonString) throws JsonSyntaxException {
			JsonElement json = jsonParser.parse(jsonString);
			if(!json.isJsonObject()) {
				logger.error(MessageKey.AQWEGA14003E_expected_object_when_parse_json_response.getMessage());
				return getDefaultReturnValue();
			}
			
			JsonObject jsonObject = json.getAsJsonObject();
			if(!jsonObject.has("classifiers")) {
				logger.error(MessageKey.AQWEGA14004E_missing_key_classifiers.getMessage());
				return getDefaultReturnValue();
			}
			
			final Type classifierListType = new TypeToken<List<NLClassifierImpl>>() {}.getType();
			List<NLClassifier> classifiers = gson.fromJson(jsonObject.get("classifiers"), classifierListType);
			
			for( NLClassifier classifier : classifiers) {
				((NLClassifierImpl) classifier).setRestClient(classifierClient);
			}
			return classifiers;
		}
		
		@Override
		protected void validate(List<NLClassifier> classifiers) throws InvalidObjectException {
			for(NLClassifier classifier : classifiers) {
				if(classifier.getId() == null || classifier.getId().isEmpty()) {
					throw new InvalidObjectException(MessageKey.AQWEGA14005E_classifiers_response_null_id.getMessage().getFormattedMessage());
				}
			}
		}
		
		@Override
		public List<NLClassifier> getDefaultReturnValue() {
			return Collections.emptyList();
		}
	};
	private final JSONResponseHandler<NLClassifier> classifierResponseHandler = new NLClassifierJsonResponseHandler<NLClassifier>(this) {
		@Override
		protected NLClassifier parseJSON(String jsonString) throws JsonSyntaxException {
			NLClassifierImpl classifier = gson.fromJson(jsonString, NLClassifierImpl.class);
			classifier.setRestClient(classifierClient);
			return classifier;
		}
		@Override
		protected void validate(NLClassifier classifier) throws InvalidObjectException {
			if(classifier.getId() == null || classifier.getId().isEmpty()) {
				throw new InvalidObjectException(MessageKey.AQWEGA14005E_classifiers_response_null_id.getMessage().getFormattedMessage());
			}
		}
	};
	private final JSONResponseHandler<NLClassifierStatusResponse> classifierStatusResponseHandler = new JSONResponseHandler<NLClassifierStatusResponse>(gson, NLClassifierStatusResponse.class) {
		@Override
		protected void validate(NLClassifierStatusResponse object) throws InvalidObjectException {
			if(object.getClassifierId() == null || object.getClassifierId().isEmpty()) {
				throw new InvalidObjectException(MessageKey.AQWEGA14005E_classifiers_response_null_id.getMessage().getFormattedMessage());
			}
			if(object.getStatus() == null) {
				throw new InvalidObjectException(MessageKey.AQWEGA14006E_status_response_null.getMessage().getFormattedMessage());
			}
		}
		
		@Override
		public NLClassifierStatusResponse getDefaultReturnValue() {
			NLClassifierStatusResponse response = new NLClassifierStatusResponse();
			response.setStatus(Status.UNKNOWN);
			response.setStatusDescription("The status of the classifier could not be properly determined");
			return response;
		}
	};
	private final JSONResponseHandler<NLClassiferClassifyResponse> classifyResponseHandler = new JSONResponseHandler<NLClassiferClassifyResponse>(gson, NLClassiferClassifyResponse.class) {
		@Override
		protected void validate(NLClassiferClassifyResponse object) throws InvalidObjectException {
			if(object.getClasses() == null) {
				throw new InvalidObjectException(MessageKey.AQWEGA14007E_classify_response_cannot_have_null_classlist.getMessage().getFormattedMessage());
			}
		}
		
		@Override
		public NLClassiferClassifyResponse getDefaultReturnValue() {
			NLClassiferClassifyResponse response = new NLClassiferClassifyResponse();
			response.setClasses(Collections.<NLClassifiedClass>emptyList());
			return response;
		}
	};	
	private final BooleanResponseHandler booleanHTTPStatusResponseHandler = new BooleanResponseHandler();
	
	public NLClassifierRestClient(NLClassifierCredentials creds) {
		this(creds.getUrl(), creds.getUsername(), creds.getPassword());
	}

	public NLClassifierRestClient(String url, String username, String password) {
		super(url, username, password);
	}
	
	// Used in testing
	NLClassifierRestClient(String url, CloseableHttpClient client) {
		super(url, client);
	}
	
	public NLClassifier createClassifier(NLClassifierTrainingData trainingData) {
		// the classifier is currently trained using cURL commands
		// for now return a default value until we have a need for this to be implemented
		return classifierResponseHandler.getDefaultReturnValue();
	}
	
	public List<NLClassifier> getClassifiers() {
		try {
			return get(CLASSIFIERS_ENDPOINT, classifiersResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14009E_error_when_trying_fetch_classifiers_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return classifiersResponseHandler.getDefaultReturnValue();
	}
	
	public NLClassiferClassifyResponse classify(String id, String text) {
		try {
			HttpEntity entity = JSONEntity.create(new NLClassifierClassifyRequest(text));
			return post( getEndpoint(CLASSIFY_ENDPOINT, id), entity, classifyResponseHandler);
		} catch(IOException e) {
            logger.error(MessageKey.AQWEGA14010E_error_when_trying_classify_text_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return classifyResponseHandler.getDefaultReturnValue();
	}
	
	public boolean deleteClassifier(String id) {
		try {
			return delete( getEndpoint(CLASSIFIER_ENDPOINT, id), booleanHTTPStatusResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14011E_error_when_trying_delete_classifier_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return booleanHTTPStatusResponseHandler.getDefaultReturnValue();
	}
	
	public NLClassifier getClassifier(String id) {
		try {
			return get( getEndpoint(CLASSIFIER_ENDPOINT, id), classifierResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14012E_error_when_trying_fetch_classifier_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return classifierResponseHandler.getDefaultReturnValue();
	}
	
	public NLClassifierStatusResponse getClassifierStatus(String id) {
		try {
			return get( getEndpoint(CLASSIFIER_ENDPOINT, id), classifierStatusResponseHandler);
		} catch(IOException e) {
			logger.error(MessageKey.AQWEGA14013E_error_when_trying_fetch_classifier_status_1.getMessage(e.getMessage()));
			logger.catching(e);
		}
		return classifierStatusResponseHandler.getDefaultReturnValue();
	}
	
	private String getEndpoint( String endpoint, String id) {
		return endpoint.replace("${classifier_id}", id);
	}

	/**
	 * This response handler keeps a reference to the client that requested the data.
	 * This allows it to be passed along to objects created in {@link #handleResponse(HttpResponse)}.
	 */
	private abstract class NLClassifierJsonResponseHandler<T> extends JSONResponseHandler<T> {
		protected final NLClassifierRestClient classifierClient; // Protected so subclasses can access

		public NLClassifierJsonResponseHandler(NLClassifierRestClient classifierClient) {
			super(gson, null);
			this.classifierClient = classifierClient;
		}
		
		@Override
		protected abstract T parseJSON(String jsonString) throws JsonSyntaxException; // Force subclasses to not use the default impl of this
	}
}
