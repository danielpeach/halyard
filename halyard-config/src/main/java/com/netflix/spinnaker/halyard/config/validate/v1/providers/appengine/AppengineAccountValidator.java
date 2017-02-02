/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.config.validate.v1.providers.appengine;

import com.netflix.spinnaker.clouddriver.appengine.security.AppengineNamedAccountCredentials;
import com.netflix.spinnaker.halyard.config.model.v1.node.Validator;
import com.netflix.spinnaker.halyard.config.model.v1.problem.Problem.Severity;
import com.netflix.spinnaker.halyard.config.model.v1.problem.ProblemSetBuilder;
import com.netflix.spinnaker.halyard.config.model.v1.providers.appengine.AppengineAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppengineAccountValidator extends Validator<AppengineAccount> {
  @Autowired
  String halyardVersion;
  
  @Override
  public void validate(ProblemSetBuilder p, AppengineAccount account) {
    String jsonPath = account.getJsonPath();
    String project = account.getProject();
    AppengineNamedAccountCredentials credentials = null;

    if (isSet(account.getGitHttpsPassword()) != isSet(account.getGitHttpsUsername())) {
      if (!isSet(account.getGitHttpsPassword())) {
        p.addProblem(Severity.ERROR, "Git HTTPS password supplied without git HTTPS username.");  
      } else {
        p.addProblem(Severity.ERROR, "Git HTTPS username supplied without git HTTPS password.");
      }
    }

    if (isSet(account.getSshPrivateKeyPassword()) != isSet(account.getSshPrivateKeyFilePath())) {
      if (!isSet(account.getSshPrivateKeyPassword())) {
        p.addProblem(Severity.ERROR, "SSH private key password supplied without SSH private key filepath.");
      } else {
        p.addProblem(Severity.ERROR, "SSH private key filepath supplied without SSH private key password.");
      }
    } else if (isSet(account.getSshPrivateKeyPassword()) && isSet(account.getSshPrivateKeyFilePath())) {
      new FilePathValidator().setFilePath(account.getSshPrivateKeyFilePath()).setProblemSetBuilder(p)
              .setEmptyWarningMessage("The supplied SSH private key file is empty")
              .setNotFoundErrorMessage("SSH private key not found")
              .setIoExceptionErrorMessage("Error opening path to SSH private key")
              .validateAndReturnFileContents();
    }

    String jsonKey = new FilePathValidator().setFilePath(jsonPath).setProblemSetBuilder(p)
            .setEmptyWarningMessage("The supplied credentials file is empty")
            .setNotFoundErrorMessage("Json path not found")
            .setIoExceptionErrorMessage("Error opening specified json path")
            .validateAndReturnFileContents();

    if (!isSet(account.getProject())) {
      p.addProblem(Severity.ERROR, "No appengine project supplied.");
      return;
    }
    
    try {
      credentials = new AppengineNamedAccountCredentials.Builder()
              .jsonKey(jsonKey)
              .project(project)
              .applicationName("halyard " + halyardVersion)
              .build();
              
    } catch (Exception e) {
      p.addProblem(Severity.ERROR, "Error instantiating App Engine credentials: " + e.getMessage() + ".");
      return;
    }
    
    try {
      credentials.getAppengine().apps().locations().list("-").execute();
    } catch (Exception e) {
      p.addProblem(Severity.ERROR, "Failed to connect to App Engine Admin API: " + e.getMessage() + ".");
    }
  }
  
  private static boolean isSet(String val) {
    return val != null && !val.isEmpty();
  }
}
