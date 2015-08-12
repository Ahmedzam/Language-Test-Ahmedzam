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

import java.util.Objects;

import com.ibm.watson.app.common.services.nlclassifier.NLClassifier;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierStatusResponse;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class NLClassifierImpl implements NLClassifier {
	private String classifierId;
	private String url;
	private Status status;
	private String statusDescription;

	// We set this on deserialization in the rest client, marked transient so it's never serialized
	private transient NLClassifierRestClient client;
	
	NLClassifierImpl(String classifierId) {
		this.classifierId = classifierId; // Used in testing
	}

	/**
	 * Set the REST client that was used to fetch this NLClassifier data.
	 * 
	 * @param client
	 */
	void setRestClient(NLClassifierRestClient client) {
		this.client = client;
	}
	
	/**
	 * Update the status details of this NLClassifier object
	 */
	private void updateStatus() {
		NLClassifierStatusResponse statusPayload = client.getClassifierStatus(getId());
		status = statusPayload.getStatus();
		statusDescription = statusPayload.getStatusDescription();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.watson.app.common.services.nlclassifier.NLClassifier#classify(java.lang.String)
	 */
	@Override
	public NLClassiferClassifyResponse classify(String text) {
		Objects.requireNonNull(client, MessageKey.AQWEGA14101E_rest_clien_not_established.getMessage().getFormattedMessage());
		return client.classify(getId(), text);
	}

	/* (non-Javadoc)
	 * @see com.ibm.watson.app.common.services.nlclassifier.NLClassifier#delete()
	 */
	@Override
	public boolean delete() {
		Objects.requireNonNull(client, MessageKey.AQWEGA14101E_rest_clien_not_established.getMessage().getFormattedMessage());
		return client.deleteClassifier(getId());
	}

	/* (non-Javadoc)
	 * @see com.ibm.watson.app.common.services.nlclassifier.NLClassifier#getId()
	 */
	@Override
	public String getId() {
		return classifierId;
	}

	public String getUrl() {
		return url;
	}

	/* (non-Javadoc)
	 * @see com.ibm.watson.app.common.services.nlclassifier.NLClassifier#getStatus()
	 */
	@Override
	public Status getStatus() {
		updateStatus();
		return status;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("id").append("=").append(classifierId).append("\n");
		builder.append("\t").append("url").append("=").append(url).append("\n");
		builder.append("\t").append("status").append("=").append(status).append("\n");
		builder.append("\t").append("description").append("=").append(statusDescription).append("\n");
		return builder.toString();
	}
}
