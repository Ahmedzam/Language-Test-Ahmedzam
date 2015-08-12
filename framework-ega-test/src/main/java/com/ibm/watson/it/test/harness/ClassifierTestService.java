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

package com.ibm.watson.it.test.harness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierClassifyRequest;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse.NLClassifiedClass;
import com.ibm.watson.it.test.harness.model.ClassifierTestAssertData;
import com.ibm.watson.it.test.harness.model.ClassifierTestAssertErrorData;
import com.ibm.watson.it.test.harness.model.ClassifierTestData;
import com.ibm.watson.it.test.harness.model.NLClassifierStatus;

@Path("/v1")
public class ClassifierTestService implements IClassifierTestHarnessService {

    private static final Logger logger = LogManager.getLogger();

    public static final String CLASSIFIER_ID = "12345-abc-1";
    public static final String TRAINING_CLASSIFIER_ID = "12345-abc-1-training";


    private static OkClass OK = new OkClass();
    static final Map<String, ClassifierTestData> mapOfResponses = new ConcurrentHashMap<>();
    static final Map<String, String> mapOfClassifierStatus = new ConcurrentHashMap<>();

    static final ClassifierTestAssertData emptyClassifyResponse = new ClassifierTestAssertData();

    static {
        mapOfClassifierStatus.put(CLASSIFIER_ID, "Available");

        NLClassiferClassifyResponse cr = new NLClassiferClassifyResponse();
        emptyClassifyResponse.setResponse(cr);

        // return a default class for every question in this service (for testing)
        NLClassifiedClass cl = new NLClassifiedClass("defaultClass", 0.9);
        List<NLClassifiedClass> classes = new ArrayList<NLClassifiedClass>();
        classes.add(cl);
        cr.setClasses(classes);
        cr.setClassifierId(CLASSIFIER_ID);
        cr.setText("*");
        cr.setTopClass(cl.getClassName());
        cr.setUrl("localhost:/" + CLASSIFIER_ID);
    }


    @GET
    @Path("/classifiers")
    @Produces({"application/json"})
    public Response getClassifiers() {

        logger.entry();

        JsonObject json = new JsonObject();
        JsonArray arr = new JsonArray();
        JsonObject c = new JsonObject();
        c.addProperty("classifier_id", CLASSIFIER_ID);
        c.addProperty("status", "Available");
        c.addProperty("url", "localhost:/" + CLASSIFIER_ID);
        arr.add(c);
        json.add("classifiers", arr);
        String jsonStr = new Gson().toJson(json);
        logger.exit(jsonStr);
        return getOkResponse(jsonStr);
    }


    @POST
    @Path("/classifiers")
    @Produces({"application/json"})
    public Response trainClassifier(NLClassifierTrainingData payload) {

        logger.entry();

        mapOfClassifierStatus.put(TRAINING_CLASSIFIER_ID, "Training");
        NLClassifierStatus status = new NLClassifierStatus();
        status.setClassifierId(TRAINING_CLASSIFIER_ID);
        status.setStatus(mapOfClassifierStatus.get(TRAINING_CLASSIFIER_ID));
        status.setUrl("localhost:/" + TRAINING_CLASSIFIER_ID);
        logger.exit(status);
        return getOkResponse(status);
    }

    @DELETE
    @Path("/classifiers/{classifier_id}")
    @Produces({"application/json"})
    public Response deleteClassifier(@PathParam("classifier_id") String classifier_id) {

        logger.entry();

        mapOfClassifierStatus.remove(classifier_id);

        logger.exit();
        return getOkResponse(OK);
    }

    @GET
    @Path("/classifiers/{classifier_id}")
    @Produces({"application/json"})
    public Response getClassifier(@PathParam("classifier_id") String classifier_id) {

        logger.entry();

        NLClassifierStatus resp = new NLClassifierStatus();
        String status = mapOfClassifierStatus.get(classifier_id);
        if (status == null) {
            status = "Non Existent";
        }
        resp.setStatus(status);
        resp.setClassifierId(classifier_id);
        resp.setUrl("http://localhost/" + classifier_id);

        logger.exit(resp);
        return getOkResponse(resp);
    }


    @POST
    @Path("/classifiers/{classifier_id}/classify")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response classify(
            @PathParam("classifier_id") String classifier_id,
            NLClassifierClassifyRequest req) {

        logger.entry(classifier_id, req);

        ClassifierTestData resp = mapOfResponses.get(req.getText());
        if (resp == null) {
            resp = emptyClassifyResponse;
            resp.setText(req.getText());
        }

        logger.exit(resp);
        return resp.getResponse();
    }


    @POST
    @Path("/assertClassify")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response assertClassify(ClassifierTestAssertData req) {

        logger.entry(req);
        mapOfResponses.put(req.getText(), req);

        return getOkResponse(OK);
    }

    @POST
    @Path("/assertClassifyError")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response assertClassifyError(ClassifierTestAssertErrorData req) {

        logger.entry(req);
        mapOfResponses.put(req.getText(), req);

        return getOkResponse(OK);
    }


    @GET
    @Path("/classifiers/{classifier_id}/assertStatus/{status}")
    @Produces({"application/json"})
    public Response setClassifierStatus(@PathParam("classifier_id") String id, @PathParam("status") String status) {

        logger.entry(id, status);
        mapOfClassifierStatus.put(id, status);

        logger.exit(mapOfClassifierStatus.get(id));
        return getOkResponse(OK);
    }

    Response getOkResponse(Object entity) {
        final ResponseBuilder builder = Response.ok()
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache");
        if (entity != null) {
            builder.entity(entity);
        }
        return builder.build();
    }

    static class OkClass {
        String ok = "ok";

        public String getOk() {
            return ok;
        }

        public void setOk(String ok) {
            this.ok = ok;
        }

    }
}
