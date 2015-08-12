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

package com.ibm.watson.app.common.tagEvent;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import com.google.common.collect.Iterators;
import com.google.gson.annotations.Expose;

/**
 * Event record representing a user-submitted query
 */
public class QueryTagRecord extends EventTagRecord {

    @Expose
    private String string;
    @Expose
    private InputMethod mode;

    public QueryTagRecord(Date timestamp, String user, String string, InputMethod mode) {
        super(timestamp, user);
        setString(string);
        setMode(mode);
        setType("query");
    }

    @Override
    public Iterator<String> iterator() {
        return Iterators.concat(super.iterator(),
                Arrays.asList(getString(), getMode().toString()).iterator());
    }

    @Override
    protected void prepareForFormat() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public InputMethod getMode() {
        return mode;
    }

    public void setMode(InputMethod mode) {
        this.mode = mode;
    }

    /**
     * Whether the user submitted the query by typing it into a text input, or clicked a suggested query
     */
    public static enum InputMethod {
        clicked, typed
    }

}
