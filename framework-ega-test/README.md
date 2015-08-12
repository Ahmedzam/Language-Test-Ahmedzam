# framework-ega-test
Contains common test classes.

The classes in this project can be packaged into a WAR and used as a mock NL Classifier service.

## Using the mock classifier service
In order to use the mock classifier service you should create a WAR project that depends on this project.
The instructions below assume you have deployed an application on port 9080 with context root /test.

### Configuring your main application to use the mock classifier service
You can override the VCAP_SERVICES environment variable to point to the mock classifier service.

`VCAP_SERVICES={"natural_language_classifier.dev": [{"credentials": {"url": "http://localhost:9080/test/testharness", "username": "", "password": ""}, "plan": "free", "name": "watson-app-qaclassifier-classifier", "label": "natural_language_classifier.dev"}]}`

### Interacting with the mock classifier service
The mock classifier service implements the same APIs as the classifier service.

You can list classifiers:  
`$ curl http://localhost:9080/test/testharness/v1/classifiers`  
<pre>
{
    "classifiers": [
        {
            "classifier_id": "12345-abc-1",
            "status": "Available",
            "url": "localhost:/12345-abc-1"
        }
    ]
}
</pre>

You can classify a string:  
`curl -X POST -H "Content-Type:application/json" http://localhost:9080/test/testharness/v1/classifiers/12345-abc-1/classify -d '{ "text": "text to classify" }'`
<pre>
{
    "classes": [
        {
            "class_name": "defaultClass",
            "confidence": 0.5
        }
    ],
    "classifier_id": "12345-abc-1",
    "text": "*",
    "top_class": "defaultClass",
    "url": "localhost:/12345-abc-1"
}
</pre>

You can mock a response for a specific string:
`curl -X POST http://localhost:9080/test/testharness/v1/assertClassify -d '{"text":"What is the NL Classifier service?","response":{"url": "", "text": "", "classes": [{"class_name": "A mocked response", "confidence": 0}], "classifier_id": "foo", "top_class": ""}}' --header "Content-Type: application/json"`
<pre>
{"ok":"ok"}
</pre>

And you can retrieve that response:
`curl -X POST -H "Content-Type:application/json" http://localhost:9080/test/testharness/v1/classifiers/12345-abc-1/classify -d '{ "text": "What is the NL Classifier service?" }'`
<pre>
{
    "classes": [
        {
            "class_name": "A mocked response",
            "confidence": 0.0
        }
    ],
    "classifier_id": "foo",
    "text": "",
    "top_class": "",
    "url": ""
}
</pre>