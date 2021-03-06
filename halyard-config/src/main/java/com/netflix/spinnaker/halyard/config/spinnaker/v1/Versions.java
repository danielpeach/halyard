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

package com.netflix.spinnaker.halyard.config.spinnaker.v1;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Versions {
  @Data
  public static class Version {
    String version;
    String alias;
    String changelog;

    @Override
    public String toString() {
      return alias + " (" + version + "): " + changelog;
    }
  }

  String latest;
  List<Version> versions = new ArrayList<>();
}
