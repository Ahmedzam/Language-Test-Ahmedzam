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

package com.ibm.watson.app.common.services.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.app.common.services.BluemixConfiguredService;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class BluemixServicesConfigurationParser {
	private static final Logger logger = LogManager.getLogger();
	
	private final Gson gson = new GsonBuilder()
		.setVersion(1.0)
		.create();
	
	public void parseAndRegisterServices(String jsonString) {
		Objects.requireNonNull(jsonString, MessageKey.AQWEGA14018E_json_string_null.getMessage().getFormattedMessage());
		
		if(logger.isDebugEnabled()) {
			logger.debug("Parsing JSON string: " + jsonString);
		}
		
		final JsonParser parser = new JsonParser();
		try {
		    parseAndRegisterServices(parser.parse(jsonString));
		} catch(JsonSyntaxException e) {
			logger.error(MessageKey.AQWEGA14000E_error_parsing_json_string_1.getMessage(jsonString));
		    logger.catching(e);
		}
	}
	
	public void parseAndRegisterServices(JsonElement json) {
		Objects.requireNonNull(json, MessageKey.AQWEGA14019E_json_element_null.getMessage().getFormattedMessage());
		if(!json.isJsonObject()) {
			throw new JsonParseException(MessageKey.AQWEGA14001E_expected_json_object_parse_bluemix_service_conf.getMessage().getFormattedMessage());
		}
		
		final boolean isTrace = logger.isTraceEnabled();
		
		for(Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
			final String serviceKey = entry.getKey();
			final JsonElement serviceConfigArray = entry.getValue();
			if(!serviceConfigArray.isJsonArray()) {
				throw new JsonParseException(MessageKey.AQWEGA14002E_expected_json_array_while_parsing_config_1.getMessage(serviceKey).getFormattedMessage());
			}
			
			BluemixServiceInfo<? extends BluemixConfiguredService> service = BluemixServicesBinder.getBluemixServiceByName(serviceKey);
			if(service == null) {
				logger.warn(MessageKey.AQWEGA12001W_unexpected_conf_supplied_for_service_ignore_1.getMessage(serviceKey));
				continue;
			}
			
			if(isTrace) {
				logger.trace("Loading configuration for service '" + serviceKey + "'");
			}
			
			for(JsonElement serviceInstanceConfig : serviceConfigArray.getAsJsonArray()) {
				try{
					BluemixConfiguredService serviceImpl = service.serviceImpl.getConstructor().newInstance();
					serviceImpl.setConfig(gson.fromJson(serviceInstanceConfig, serviceImpl.getConfigurationClass()));							
					serviceImpl.initialize();
					register(service, serviceImpl);
				} catch(IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					logger.warn(MessageKey.AQWEGA12002W_unable_register_service_error_2.getMessage(serviceKey, e.getMessage()));
					logger.catching(Level.DEBUG, e);
				}
			}
		}
	}

    protected void register(BluemixServiceInfo<? extends BluemixConfiguredService> service, BluemixConfiguredService serviceImpl) {
        BluemixServicesBinder.registerBluemixService(service.iface, serviceImpl);
    }
}
