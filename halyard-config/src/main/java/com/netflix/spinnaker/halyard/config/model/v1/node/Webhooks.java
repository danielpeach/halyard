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

package com.netflix.spinnaker.halyard.config.model.v1.node;

import com.netflix.spinnaker.halyard.config.model.v1.problem.ProblemSetBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Webhooks extends Node implements Cloneable {
  String empty = "ignore for now";

  @Override
  public void accept(ProblemSetBuilder psBuilder, Validator v) {
    v.validate(psBuilder, this);
  }

  @Override
  public String getNodeName() {
    return "webhooks";
  }

  @Override
  public NodeIterator getChildren() {
    return NodeIteratorFactory.makeReflectiveIterator(this);
  }

  @Override
  protected boolean matchesLocally(NodeFilter filter) {
    return !filter.webhook.isEmpty();
  }

  @Override
  public NodeFilter getFilter() {
    return parent.getFilter();
  }
}
