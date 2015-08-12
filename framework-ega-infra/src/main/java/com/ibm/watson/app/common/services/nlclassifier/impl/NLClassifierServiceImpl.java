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

import java.util.List;

import com.ibm.watson.app.common.services.bluemix.BaseBluemixService;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifierService;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData;

public class NLClassifierServiceImpl extends BaseBluemixService<NLClassifierConfiguration> implements NLClassifierService {
	private NLClassifierRestClient client;

	public NLClassifierServiceImpl() {
		super(NLClassifierConfiguration.class);
	}

	// Used for testing
	NLClassifierServiceImpl(NLClassifierRestClient client) {
		this();
		this.client = client;
	}

	@Override
	public void initialize() {
		client = new NLClassifierRestClient(config.getCredentials());
	}

	@Override
	public NLClassifier createClassifier(NLClassifierTrainingData trainingData) {
		return client.createClassifier(trainingData);
	}

	@Override
	public List<NLClassifier> getClassifiers() {
		return client.getClassifiers();
	}

	@Override
	public NLClassifier getClassifier(String classifierId) {
		return client.getClassifier(classifierId);
	}
}
