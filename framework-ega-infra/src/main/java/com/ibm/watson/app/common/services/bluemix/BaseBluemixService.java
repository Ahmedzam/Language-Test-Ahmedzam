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

package com.ibm.watson.app.common.services.bluemix;

import java.util.List;

import com.ibm.watson.app.common.services.BluemixConfiguredService;

public abstract class BaseBluemixService<T extends BaseBluemixServiceConfiguration> implements BluemixConfiguredService {

	protected T config;
	private Class<T> configImpl;

	public BaseBluemixService(Class<T> configImpl) {
		this.configImpl = configImpl;
	}

	@Override
	public void setConfig(Object object) {
		this.config = configImpl.cast(object);
	}

	@Override
	public Class<T> getConfigurationClass() {
		return configImpl;
	}

	@Override
	public String getName() {
		return config.getName();
	}

	@Override
	public String getLabel() {
		return config.getLabel();
	}

	@Override
	public List<String> getTags() {
		return config.getTags();
	}

	@Override
	public String getPlan() {
		return config.getPlan();
	}
}
