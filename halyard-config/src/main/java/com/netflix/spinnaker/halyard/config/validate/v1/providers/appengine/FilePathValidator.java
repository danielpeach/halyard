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

import com.amazonaws.util.IOUtils;
import com.netflix.spinnaker.halyard.config.model.v1.problem.Problem;
import com.netflix.spinnaker.halyard.config.model.v1.problem.ProblemSetBuilder;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FilePathValidator {
  @Setter
  private String filePath;
  @Setter
  private ProblemSetBuilder problemSetBuilder;
  @Setter
  private String emptyWarningMessage;
  @Setter
  private String notFoundErrorMessage;
  @Setter
  private String ioExceptionErrorMessage;
  
  public String validateAndReturnFileContents() {
    String fileContents = null;
    try {
      if (isSet(filePath)) {
        fileContents = IOUtils.toString(new FileInputStream(filePath));
        if (!isSet(fileContents)) {
          problemSetBuilder.addProblem(Problem.Severity.WARNING, emptyWarningMessage + ".");
        }
      }
    } catch (FileNotFoundException e) {
      problemSetBuilder.addProblem(Problem.Severity.ERROR, notFoundErrorMessage + ": " + e.getMessage() + ".");
    } catch (IOException e) {
      problemSetBuilder.addProblem(Problem.Severity.ERROR, ioExceptionErrorMessage + ": " + e.getMessage() + ".");
    }
    return fileContents;
  }

  private static boolean isSet(String val) {
      return val != null && !val.isEmpty();
  }
}
