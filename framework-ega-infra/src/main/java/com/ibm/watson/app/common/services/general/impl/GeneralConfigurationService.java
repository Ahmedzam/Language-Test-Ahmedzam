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

package com.ibm.watson.app.common.services.general.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import com.ibm.watson.app.common.services.general.ConfigurationService;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class GeneralConfigurationService implements ConfigurationService {
    private final Properties properties;
    
    public GeneralConfigurationService(Properties properties) {
        Objects.requireNonNull(properties, MessageKey.AQWEGA04003E_prop_cannot_be_null.getMessage().getFormattedMessage());
        this.properties = properties;
    }

    public GeneralConfigurationService(InputStream stream) throws IOException {
        Objects.requireNonNull(stream, MessageKey.AQWEGA04004E_stream_cannot_be_null.getMessage().getFormattedMessage());
        
        this.properties = new Properties();
        properties.load(stream);
    }

    @Override
    public String getProperty(String propName) {
        return properties.getProperty(propName);
    }

    @Override
    public String getProperty(String propName, String defaultValue) {
        return properties.getProperty(propName, defaultValue);
    }
}
