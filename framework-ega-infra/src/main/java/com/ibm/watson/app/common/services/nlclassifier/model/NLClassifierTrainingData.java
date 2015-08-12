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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Since;

public class NLClassifierTrainingData {
	private static transient final Gson gson = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.setVersion(1.0)
			.create();

	@Since(1.0)
	private String language;

	@Since(1.0)
	private String name;

	@Since(1.0)
	private List<TrainingInstance> trainingData;

	public String toJson() {
		return gson.toJson(this);
	}

	public static NLClassifierTrainingData fromFile(File file) throws IOException {
		try(InputStream stream = new FileInputStream(file)) {
			return fromStream(stream);
		}
	}

	public static NLClassifierTrainingData fromStream(InputStream stream) throws IOException {
		try(Reader reader = new InputStreamReader(stream)) {
			return gson.fromJson(reader, NLClassifierTrainingData.class);
		}
	}

    @JsonProperty("language")
	public String getLanguage() {
		return language;
	}

    @JsonSetter("language")
	public void setLanguage(String language) {
		this.language = language;
	}

    @JsonProperty("name")
	public String getName() {
		return name;
	}

    @JsonSetter("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("training_data")
	public List<TrainingInstance> getTrainingData() {
		return trainingData;
	}
	
    @JsonSetter("training_data")
	public void setTrainingData(List<TrainingInstance> trainingData) {
		this.trainingData = trainingData;
	}

	public void addTrainingData( String text, String... classes) {
      if ( trainingData == null ) { 
         trainingData = new ArrayList<>();
      }
      trainingData.add( new TrainingInstance( text, classes));
   }
   
	public static class TrainingInstance {
		@Since(1.0)
		private String text;
		
		@Since(1.0)
		private List<String> classes;

        public TrainingInstance() {}

		public TrainingInstance(String text, String... classes) {
			this.text = text;
			this.classes = Arrays.asList(classes);
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public List<String> getClasses() {
			return classes;
		}

		public void setClasses(List<String> classes) {
			this.classes = classes;
		}
	}
}
