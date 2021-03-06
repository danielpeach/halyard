/*
 * Copyright 2016 Google, Inc.
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

package com.netflix.spinnaker.halyard.config.model.v1.problem;

import com.netflix.spinnaker.halyard.config.model.v1.node.NodeFilter;
import java.util.List;
import lombok.Getter;

/**
 * This represents a single "problem" with the currently loaded/modified halconfig.
 */
public class Problem {
  public enum Severity {
    /**
     * Indicates no problem at all. This exists as a point of comparison against the greater severity levels, and
     * may not be used to instantiate a problem.
     */
    NONE,

    /**
     * Indicates the deployment of Spinnaker is going against our preferred/recommended practices.
     * For example: using an unauthenticated docker registry.
     */
    WARNING,

    /**
     * Indicates the deployment of Spinnaker will fail as-is (but the request can be performed).
     * For example: using an incorrect password in your docker registry.
     */
    ERROR,

    /**
     * Indicates this request cannot hope to be performed.
     * For example: asking to update an account that doesn't exist.
     */
    FATAL,
  }

  /**
   * The location of the problem in the config.
   */
  @Getter
  final private NodeFilter filter;

  /**
   * Provides a human-readable filter interpretation
   */
  public String getFilterTitle() {
    if (filter != null) {
      return filter.toString();
    } else {
      return "Global";
    }
  }

  /**
   * A human-readable message describing the problem.
   */
  @Getter
  final private String message;

  /**
   * An optional human-readable message describing how to fix the problem.
   */
  @Getter
  final private String remediation;

  /**
   * An optional list of alternative entries.
   */
  @Getter
  final private List<String> options;

  /**
   * Indicates if this will cause the deployment to fail or not.
   */
  @Getter
  final private Severity severity;

  public Problem(Severity severity, NodeFilter filter, String message, String remediation, List<String> options) {
    if (severity == Severity.NONE) {
      throw new RuntimeException("A halconfig problem may not be intialized with \"NONE\" severity");
    }
    this.severity = severity;
    this.filter = filter;
    this.message = message;
    this.remediation = remediation;
    this.options = options;
  }
}
