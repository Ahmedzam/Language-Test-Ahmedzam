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

package com.ibm.watson.app.common.services.nlclassifier;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;

public interface NLClassifier {
	/**
	 * Enumerates all valid statuses of a classifier
	 */
	public enum Status {
		@SerializedName("Non Existent")
        @JsonProperty("Non Existent")
		NON_EXISTENT,
		@SerializedName("Training")
        @JsonProperty("Training")
		TRAINING,
		@SerializedName("Failed")
        @JsonProperty("Failed")
		FAILED,
		@SerializedName("Available")
        @JsonProperty("Available")
		AVAILABLE,
		@SerializedName("Unavailable")
        @JsonProperty("Unavailable")
		UNAVAILABLE,
		// Returned when the client was unable to fetch the status from the server
		UNKNOWN;
	}
	
	/**
	 * Get the unique classifier ID for this classifier
	 * @return String The classifier ID
	 */
	public String getId();
	
	/**
	 * Get the current status of this classifier
	 * <p>This method will submit a request to the service to get the current status of the classifier.</p>
	 * @return Status The Status constant corresponding to the current status of this classifier
	 */
	public Status getStatus();

	/**
	 * Submit a classify request to the classifier service for this classifier
	 * @param text The text to classify
	 * @return NLClassifyOutputPayload An object representing the classification results from the classifier
	 * @throws IOException if an error occurred communicating with the NLClassifier service
	 */
	public NLClassiferClassifyResponse classify(String text);

	/**
	 * Delete this classifier instance
	 * @return boolean true on success, false on failure
	 * @throws IOException if an error occurred communicating with the NLClassifier service
	 */
	public boolean delete();
}
