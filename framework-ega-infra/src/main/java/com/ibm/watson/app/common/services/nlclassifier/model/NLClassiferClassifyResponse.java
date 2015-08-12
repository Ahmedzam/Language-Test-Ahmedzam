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

package com.ibm.watson.app.common.services.nlclassifier.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Since;

public class NLClassiferClassifyResponse {
	@Since(1.0)
	private String classifierId;

	@Since(1.0)
	private String url;

	@Since(1.0)
	private String text;

	@Since(1.0)
	private String topClass;

	@Since(1.0)
	private List<NLClassifiedClass> classes;

    @JsonProperty("classifier_id")
	public String getClassifierId() {
		return classifierId;
	}

    @JsonSetter("classifier_id")
	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("top_class")
	public String getTopClass() {
		return topClass;
	}

    @JsonSetter("top_class")
	public void setTopClass(String topClass) {
		this.topClass = topClass;
	}

	public List<NLClassifiedClass> getClasses() {
		return classes;
	}

	public void setClasses(List<NLClassifiedClass> classes) {
		this.classes = classes;
	}

	public static class NLClassifiedClass {
		private String className;
		private Double confidence;

		public NLClassifiedClass() {}
		
		public NLClassifiedClass(String className, Double confidence) {
			this.className = className;
			this.confidence = confidence;
		}

	    @JsonProperty("class_name")
		public String getClassName() {
			return className;
		}

        @JsonSetter("class_name")
		public void setClassName(String className) {
			this.className = className;
		}

		public Double getConfidence() {
			return confidence;
		}

		public void setConfidence(Double confidence) {
			this.confidence = confidence;
		}
	}
}
