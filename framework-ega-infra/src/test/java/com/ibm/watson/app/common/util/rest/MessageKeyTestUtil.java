/*
 * Copyright IBM Corp. 2015
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.common.util.rest;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.message.Message;
import org.junit.rules.ErrorCollector;

public class MessageKeyTestUtil {
    public static void validateMessage(String messageKeyName, Message message, ErrorCollector errors) {

        String formattedMessage = message.getFormattedMessage();

        errors.checkThat("The formatted message contains the message key.  Is the key missing from the message bundle?",
                formattedMessage, not(containsString(messageKeyName)));

        Matcher m = Pattern.compile(".*\\{\\d+\\}").matcher(formattedMessage);
        if (m.matches()) {
            errors.checkThat("The formatted message has a {} placeholder.  Does the message key specify the wrong number of arguments?",
                    m.group(), not(anything("containing {\\d+}")));
        }

        if (message.getParameters() != null) {
            for (Object messageArg : message.getParameters()) {
                errors.checkThat("Could not find " + messageArg + " in the formatted message.  Does the message key specify the wrong number of arguments?",
                        formattedMessage, containsString(messageArg.toString()));
            }
            int argCount = message.getParameters().length;
            errors.checkThat("Message with " + argCount + " should end with _" + argCount,
                    messageKeyName, endsWith("_" + argCount));
        }
    }

    public static Object[] generateArgs(int argCount) {
        Object[] args = new Object[argCount];
        for (int i = 0; i < argCount; i++) {
            args[i] = "arg" + i;
        }
        return args;
    }
}
