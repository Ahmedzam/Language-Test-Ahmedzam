# framework-ega-test
Contains tools and utilities that can be used during development or to manage a running application.

### Training a classifier

usage: java com.ibm.watson.app.common.tools.services.classifier.TrainClassifier
 -d,--delete                If specified, the classifier instance will be deleted if training is not successful
 -f,--file <file>           The filepath to be used as training data
 -l,--url <url>             The absolute URL of the NL classifier service to connect to. If omitted, the default will be
                            used (https://gateway-d.watsonplatform.net/natural-language-classifier-alpha/api)
 -p,--password <password>   The password to use during authentication to the NL classifier service
 -u,--username <username>   The username to use during authentication to the NL classifier service
