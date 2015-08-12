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

import com.google.gson.annotations.Expose;

/**
 * Base class for events being tracked by the application
 */
public abstract class EventTagRecord extends TagRecord {

    @Expose
    private Date timestamp;
    @Expose
    private String user;

    public EventTagRecord(Date timestamp, String user) {
        setTimestamp(timestamp);
        setUser(user);
    }

    /**
     * Subclasses should combine this iterator with an iterator of their own fields.
     * 
     * e.g., com.google.common.collect.Iterators.concat(super.iterator(), Arrays.asList([instance fields]).iterator());
     */
    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(getType(), dateFormatter.get().format(getTimestamp()), getUser()).iterator();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
