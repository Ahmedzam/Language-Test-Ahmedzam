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

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;


public enum MessageKey {
   AQWEGA22100W_commit_transaction_not_in_no_transaction_state_instead_1(1),
   AQWEGA22101W_unable_commit_transaction(0),
   AQWEGA22102W_unable_begin_transaction(0),
   AQWEGA22103W_unable_rollback_transaction(0),
   AQWEGA10000I_parsing_vcap_services_json_conf_from_file_1(1),
   AQWEGA10001I_parsing_vcap_services_json_from_system_property(0),
   AQWEGA10002I_parsing_vcap_services_json_from_system_property_override(0),
   AQWEGA12000W_no_vcap_services_conf_available(0),
   AQWEGA14000E_error_parsing_json_string_1(1),
   AQWEGA14001E_expected_json_object_parse_bluemix_service_conf(0),
   AQWEGA14002E_expected_json_array_while_parsing_config_1(1),
   AQWEGA12001W_unexpected_conf_supplied_for_service_ignore_1(1),
   AQWEGA12002W_unable_register_service_error_2(2),
   AQWEGA14003E_expected_object_when_parse_json_response(0),
   AQWEGA14004E_missing_key_classifiers(0),
   AQWEGA14005E_classifiers_response_null_id(0),
   AQWEGA14006E_status_response_null(0),
   AQWEGA14007E_classify_response_cannot_have_null_classlist(0),
   AQWEGA14008E_error_when_trying_create_classifier_1(1),
   AQWEGA14009E_error_when_trying_fetch_classifiers_1(1),
   AQWEGA14010E_error_when_trying_classify_text_1(1),
   AQWEGA14011E_error_when_trying_delete_classifier_1(1),
   AQWEGA14012E_error_when_trying_fetch_classifier_1(1),
   AQWEGA14013E_error_when_trying_fetch_classifier_status_1(1),
   AQWEGA14200E_error_box_search_request_1(1),
   AQWEGA14201E_error_box_metadata_request_1(1),
   AQWEGA14202E_error_box_content_request_1(1),
   AQWEGA02000W_unable_init_ssl_context(0),
   AQWEGA02001W_received_invalid_http_status_2(2),
   AQWEGA04000E_error_while_handling_resoinse_entity_1(1),
   AQWEGA04001E_unable_parse_json_1(1),
   AQWEGA04002E_multiple_instances_of_service_found_1(1),
   AQWEGA14014E_username_and_password_cannot_empty(0),
   AQWEGA14015E_classifier_created_training_null(0),
   AQWEGA10003I_training_accepted_for_classifier_1(1),
   AQWEGA10004I_supported_commands(0),
   AQWEGA10005I_l_list_the_current_classifier_instances(0),
   AQWEGA10006I_t_promt_for_filename_train_classifier(0),
   AQWEGA10007I_s_get_status_currently_traininig_classifier(0),
   AQWEGA10008I_c_test_classification_with_classifier(0),
   AQWEGA10009I_q_quit_app(0),
   AQWEGA10010I_h_print_this_help(0),
   AQWEGA10011I_header_name(0),
   AQWEGA10023I_header_status(0),
   AQWEGA20000I_help_text(0),
   AQWEGA22000W_no_conf_supplied(0),
   AQWEGA20001I_loaded_conf_from_path_1(1),
   AQWEGA24000E_unable_read_conf_file_attempt_load_from_classpath_1(1),
   AQWEGA24001E_unable_read_conf_file_from_classpath_attempt_using_defaults_1(1),
   AQWEGA14016E_could_not_parse_cmd_line_args_1(1),
   AQWEGA10012I_h_help(0),
   AQWEGA10013I_t_cannot_used_during_training(0),
   AQWEGA10014I_s_can_used_during_training(0),
   AQWEGA10015I_text_classify(0),
   AQWEGA10016I_c_can_used_after_training_has_completed(0),
   AQWEGA14017E_unknown_command_1(1),
   AQWEGA10017I_classify_result_1(1),
   AQWEGA10018I_classifier_training_1(1),
   AQWEGA10019I_classifier_successfuly_trained_1(1),
   AQWEGA10020I_classifier_failed_training_status_2(2),
   AQWEGA10021I_submitted_classifier_instance_for_deletion_1(1),
   AQWEGA10022I_classifier_status_cannot_determined(0),
   AQWEGA04003E_prop_cannot_be_null(0),
   AQWEGA04004E_stream_cannot_be_null(0),
   AQWEGA14018E_json_string_null(0),
   AQWEGA14019E_json_element_null(0),
   AQWEGA14101E_rest_clien_not_established(0),
   
   
   
   ;
   
   private static final Logger logger = LogManager.getLogger();

   final static ResourceBundle bundle = ResourceBundle.getBundle("messages.messages_common");
   //final static LocalizedMessageFactory defaultMessageFactory = new LocalizedMessageFactory(ResourceBundle.getBundle("messages.Messages"));
   
   final int expectedArgs;
   
   MessageKey(int n){
      expectedArgs = n;
   }
   
   public Message getMessage() {
      return getMessage( (Object[]) null);
   }
   
   public Message getMessage( Object ...params ) {
      if ( expectedArgs != 0 && ( params == null || params.length == 0 ) ) {
         logger.error("ERROR: No arguments passed for message although message requires arguments.... " + name(),  new Exception());
      } else if ( expectedArgs == 0 && !( params == null || params.length == 0 ) ) {
         logger.error("ERROR: Arguments passed for message although message expects no arguments... " + name(),  new Exception());
      }
      return new WatsonMessage(bundle, name(), params);
   }
   
   
   
}
