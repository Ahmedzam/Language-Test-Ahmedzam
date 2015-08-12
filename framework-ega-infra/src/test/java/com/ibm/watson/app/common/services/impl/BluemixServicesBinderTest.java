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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.app.common.services.BluemixConfiguredService;
import com.ibm.watson.app.common.services.bluemix.BaseBluemixService;
import com.ibm.watson.app.common.services.bluemix.BaseBluemixServiceConfiguration;

public class BluemixServicesBinderTest {
    private final BluemixServicesBinderTest GIVEN = this, WHEN = this, THEN = this, AND = this;
    
    private String vcapJson;

    @After
    public void cleanup() {
        BluemixServicesBinder.CONFIGURED_SERVICES.clear();
        BluemixServicesBinder.AVAILABLE_SERVICES.clear();
    }
    
    @Test
    public void test_register_service_that_isnt_in_vcap() {
        GIVEN.bluemix_service_is_added_with_key("fake-service");
            AND.vcap_json_has_key("not-the-right-service");
        WHEN.service_config_is_parsed();
        THEN.verify_available_services_has_no_services();
    }

    @Test
    public void test_register_service_and_fetch_by_exact_key() {
        GIVEN.bluemix_service_is_added_with_key("test-service.dev");
            AND.vcap_json_has_key("test-service.dev");
        WHEN.service_config_is_parsed();
        THEN.verify_available_services_has_one_service();
    }
    
    @Test
    public void test_register_service_and_fetch_by_prefix() {
        GIVEN.bluemix_service_is_added_with_key("test-service");
            AND.vcap_json_has_key("test-service.dev");
        WHEN.service_config_is_parsed();
        THEN.verify_available_services_has_one_service();
    }
    
    private void verify_available_services_has_no_services() {
        assertEquals(0, BluemixServicesBinder.AVAILABLE_SERVICES.size());
    }

    private void verify_available_services_has_one_service() {
        assertTrue(BluemixServicesBinder.AVAILABLE_SERVICES.containsKey(TestService.class));
        assertEquals(1, BluemixServicesBinder.AVAILABLE_SERVICES.size());
    }

    private void service_config_is_parsed() {
        BluemixServicesBinder.parseBluemixConfig(vcapJson);
    }

    private void vcap_json_has_key(String key) {
        JsonObject obj = new JsonObject();
        JsonArray config = new JsonArray();
        config.add(new JsonObject());
        obj.add(key, config);
        vcapJson = new Gson().toJson(obj);
    }

    private void bluemix_service_is_added_with_key(String name) {
        BluemixServicesBinder.addBluemixServiceConfig(name, TestService.class, TestServiceImpl.class);
    }

    interface TestService extends BluemixConfiguredService {
    }

    public static class TestServiceImpl extends BaseBluemixService<TestServiceConfig> implements TestService {
        public TestServiceImpl() {
            super(TestServiceConfig.class);
        }

        @Override
        public void initialize() {
        }
    }
    
    class TestServiceConfig extends BaseBluemixServiceConfiguration {
    }
}
