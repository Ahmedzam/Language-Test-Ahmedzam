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

package com.ibm.watson.app.common.tools.services.classifier;

import static com.ibm.watson.app.common.tools.utils.CliUtils.buildOptions;
import static com.ibm.watson.app.common.tools.utils.CliUtils.createOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.ibm.watson.app.common.services.nlclassifier.NLClassifier;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifier.Status;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierRestClient;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassifierTrainingData;
import com.ibm.watson.app.common.services.nlclassifier.model.NLClassiferClassifyResponse.NLClassifiedClass;
import com.ibm.watson.app.common.util.rest.MessageKey;

public class TrainClassifier {
	public static final String DEFAULT_URL = "https://gateway-d.watsonplatform.net/natural-language-classifier-alpha/api";

	private static final String URL_OPTION = "l", URL_OPTION_LONG = "url";
	private static final String USERNAME_OPTION = "u", USERNAME_OPTION_LONG = "username";
	private static final String PASSWORD_OPTION = "p", PASSWORD_OPTION_LONG = "password";
	private static final String FILE_OPTION = "f", FILE_OPTION_LONG = "file";
	private static final String DELETE_OPTION = "d", DELETE_OPTION_LONG = "delete";

	public static void main(String[] args) throws Exception {
		Option urlOption = createOption(URL_OPTION, URL_OPTION_LONG, true, "The absolute URL of the NL classifier service to connect to. If omitted, the default will be used (" + DEFAULT_URL + ")", false, "url");
		Option usernameOption = createOption(USERNAME_OPTION, USERNAME_OPTION_LONG, true, "The username to use during authentication to the NL classifier service", true, "username");
		Option passwordOption = createOption(PASSWORD_OPTION, PASSWORD_OPTION_LONG, true, "The password to use during authentication to the NL classifier service", true, "password");
		Option fileOption = createOption(FILE_OPTION, FILE_OPTION_LONG, true, "The filepath to be used as training data", false, "file");
		Option deleteOption = createOption(DELETE_OPTION, DELETE_OPTION_LONG, false, "If specified, the classifier instance will be deleted if training is not successful");

		final Options options = buildOptions(urlOption, usernameOption, passwordOption, fileOption, deleteOption);

		CommandLine cmd;
		try {
			CommandLineParser parser = new GnuParser();
			cmd = parser.parse(options, args);
		} catch(ParseException e) {
			System.err.println(MessageKey.AQWEGA14016E_could_not_parse_cmd_line_args_1.getMessage(e.getMessage()).getFormattedMessage()); 
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(120, "java " + TrainClassifier.class.getName(), null, options, null);
			return;
		}

		final String url = cmd.hasOption(URL_OPTION) ? cmd.getOptionValue(URL_OPTION) : DEFAULT_URL;
		final String username = cmd.getOptionValue(USERNAME_OPTION).trim();
		final String password = cmd.getOptionValue(PASSWORD_OPTION).trim();
		
		if(username.isEmpty() || password.isEmpty()) {
		    throw new IllegalArgumentException(MessageKey.AQWEGA14014E_username_and_password_cannot_empty.getMessage().getFormattedMessage());
		}

		final NLClassifierRestClient client = new NLClassifierRestClient(url, username, password);

		listClassifiers(client);
		System.out.println(MessageKey.AQWEGA10012I_h_help.getMessage().getFormattedMessage());

		String userInput;
		boolean exit = false;
		NLClassifier classifier = null;
		boolean isTraining = false;

		if(cmd.hasOption(FILE_OPTION)) {
			// File option was specified, go directly to training
			classifier = train(client, cmd.getOptionValue(FILE_OPTION));
			isTraining = true;
		}

		try(final Scanner scanner = new Scanner(System.in)) {
			while(!exit) {
				System.out.print("> ");
				userInput = scanner.nextLine().trim();

				if(userInput.equals("q") || userInput.equals("quit") || userInput.equals("exit")) {
					exit = true;
				} else if(userInput.equals("h") || userInput.equals("help")) {
					printHelp();
				} else if(userInput.equals("l") || userInput.equals("list")) {
					listClassifiers(client);
				} else if(userInput.equals("t") || userInput.equals("train")) {
					if(!isTraining) {
						System.out.print("Enter filename: ");
						String filename = scanner.nextLine().trim();
						classifier = train(client, filename);
						isTraining = true;
					} else {
						System.err.println(MessageKey.AQWEGA10013I_t_cannot_used_during_training.getMessage().getFormattedMessage());
					}
				} else if(userInput.equals("s") || userInput.equals("status")) {
					if(isTraining) {
						exit = getStatus(client, classifier, cmd.hasOption(DELETE_OPTION));
					} else {
						System.err.println(MessageKey.AQWEGA10014I_s_can_used_during_training.getMessage().getFormattedMessage());
					}
				} else if(userInput.equals("c") || userInput.equals("classify")) {
					if(classifier != null && classifier.getStatus().equals(Status.AVAILABLE)) {
						isTraining = false;
						System.out.print(MessageKey.AQWEGA10015I_text_classify.getMessage().getFormattedMessage());
						String text = scanner.nextLine().trim();
						classify(client, classifier, text);
					} else {
						System.err.println(MessageKey.AQWEGA10016I_c_can_used_after_training_has_completed.getMessage().getFormattedMessage());
					}
				} else {
					System.err.println(MessageKey.AQWEGA14017E_unknown_command_1.getMessage(userInput).getFormattedMessage());
				}
				Thread.sleep(100); // Give the out / err consoles time to battle it out before printing the command prompt
			}
		}
	}

	private static void classify(NLClassifierRestClient client, NLClassifier trainingClassifier, String text) {
		NLClassiferClassifyResponse result = client.classify(trainingClassifier.getId(), text);
		System.out.println(MessageKey.AQWEGA10017I_classify_result_1.getMessage(text).getFormattedMessage());
		for(NLClassifiedClass c : result.getClasses()) {
			System.out.format("\t%5.2f%% - %s\n", c.getConfidence() * 100.0, c.getClassName());
		}
	}

	private static boolean getStatus(NLClassifierRestClient client, NLClassifier trainingClassifier, boolean submitForDeletionOnFailure) {
		boolean exit = false;
		Status status = trainingClassifier.getStatus();
		switch(status) {
			case TRAINING:
				System.out.println(MessageKey.AQWEGA10018I_classifier_training_1.getMessage(trainingClassifier.getId()).getFormattedMessage()); 
			break;
			case AVAILABLE:
				System.out.println(MessageKey.AQWEGA10019I_classifier_successfuly_trained_1.getMessage(trainingClassifier.getId()).getFormattedMessage());
			break;
			case FAILED:
			case NON_EXISTENT:
			case UNAVAILABLE:
				System.err.println(MessageKey.AQWEGA10020I_classifier_failed_training_status_2.getMessage(trainingClassifier.getId(), status).getFormattedMessage());
				if(submitForDeletionOnFailure) {
					client.deleteClassifier(trainingClassifier.getId());
					System.out.println(
							MessageKey.AQWEGA10021I_submitted_classifier_instance_for_deletion_1.getMessage(trainingClassifier.getId()).getFormattedMessage());
				}
				exit = true;
			break;
			case UNKNOWN:
				System.out.println(MessageKey.AQWEGA10022I_classifier_status_cannot_determined.getMessage().getFormattedMessage());
			break;
		}
		return exit;
	}

	private static NLClassifier train(NLClassifierRestClient client, String filename) throws IOException {
		File trainingDataFile = new File(filename);
		if(!trainingDataFile.exists()) {
			// No hit on absolute, try relative
			trainingDataFile = new File(".", filename);
			if(!trainingDataFile.exists()) {
				throw new FileNotFoundException(filename);
			}
		}

		System.out.println("Training with data from file: " + trainingDataFile.getAbsolutePath());
		NLClassifierTrainingData trainingData = NLClassifierTrainingData.fromFile(trainingDataFile);
		NLClassifier classifier = client.createClassifier(trainingData);
		if(classifier == null) {
			throw new IllegalStateException(MessageKey.AQWEGA14015E_classifier_created_training_null.getMessage().getFormattedMessage());
		}
		System.out.println(MessageKey.AQWEGA10003I_training_accepted_for_classifier_1.getMessage(classifier.getId()).getFormattedMessage());
		return classifier;
	}

	private static void listClassifiers(NLClassifierRestClient client) {
		final List<NLClassifier> classifiers = client.getClassifiers();
		String nameLocalized = MessageKey.AQWEGA10011I_header_name.getMessage().getFormattedMessage();
		String statusLocalize = MessageKey.AQWEGA10023I_header_status.getMessage().getFormattedMessage();
		System.out.println("Classifiers:");
		System.out.println("+----------------+--------------+");
		System.out.println("|     "+nameLocalized+"       |    "+statusLocalize+"    |");
		System.out.println("+----------------+--------------+");
		for(NLClassifier classifier : classifiers) {
			System.out.format("| %-14s | %-12s |\n", classifier.getId(), classifier.getStatus());
		}
		System.out.println("+----------------+--------------+");
	}

	private static void printHelp() {
		System.out.println(MessageKey.AQWEGA10004I_supported_commands.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10005I_l_list_the_current_classifier_instances.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10006I_t_promt_for_filename_train_classifier.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10007I_s_get_status_currently_traininig_classifier.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10008I_c_test_classification_with_classifier.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10009I_q_quit_app.getMessage().getFormattedMessage());
		System.out.println(MessageKey.AQWEGA10010I_h_print_this_help.getMessage().getFormattedMessage());
	}
}
