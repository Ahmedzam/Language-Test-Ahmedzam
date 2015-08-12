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

package com.ibm.watson.app.common.tools.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public final class CliUtils {

	public static Options buildOptions(Option option, Option ... additionalOptions) {
		final Options options = new Options();
		options.addOption(option);
		for(Option o : additionalOptions) {
			options.addOption(o);
		}
		return options;
	}
	
	public static Option createOption(String opt, String longOpt, boolean hasArg, String description) {
		return new Option(opt, longOpt, hasArg, description);
	}
	
	public static Option createOption(String opt, String longOpt, boolean hasArg, String description, boolean required, String argName) {
		Option option = new Option(opt, longOpt, hasArg, description);
		option.setRequired(required);
		option.setArgName(argName);
		return option;
	}
}
