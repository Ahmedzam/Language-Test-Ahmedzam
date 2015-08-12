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

package com.ibm.watson.it.test.harness.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Since;

public class NLClassifierStatus {

    @Since(1.0)
    private String classifierId;

    @Since(1.0)
    private String url;

    @Since(1.0)
    private String status;

    @Since(1.0)
    private String statusDescription;

    @JsonProperty("classifier_id")
    public String getClassifierId() {
        return classifierId;
    }

    @JsonSetter("classifier_id")
    public void setClassifierId(String classifierId) {
        this.classifierId = classifierId;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonSetter("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("status_description")
    public String getStatusDescription() {
        return statusDescription;
    }

    @JsonSetter("status_description")
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
