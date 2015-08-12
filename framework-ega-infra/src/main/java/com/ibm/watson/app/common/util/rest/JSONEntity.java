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

package com.ibm.watson.app.common.util.rest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A convenience class to convert an Object into a {@link StringEntity} as JSON 
 */
public class JSONEntity extends StringEntity {
	private static final Gson gson = new GsonBuilder()
	.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
	.create();
	
	public JSONEntity(String json) throws UnsupportedEncodingException {
		super(json, ContentType.APPLICATION_JSON);
	}
	
	private JSONEntity(Object object) throws UnsupportedEncodingException {
		super(gson.toJson(object), ContentType.APPLICATION_JSON);
	}
	
	private JSONEntity(Object object, Type type) throws UnsupportedEncodingException {
		super(gson.toJson(object, type), ContentType.APPLICATION_JSON);
	}
	
	public static JSONEntity create(Object object) throws UnsupportedEncodingException {
		return new JSONEntity(object);
	}
	
	public static <T> JSONEntity createFromParamaterizedType(T object) throws UnsupportedEncodingException {
		Type type = new TypeToken<T>() {}.getType();
		return new JSONEntity(object, type);
	}
}
