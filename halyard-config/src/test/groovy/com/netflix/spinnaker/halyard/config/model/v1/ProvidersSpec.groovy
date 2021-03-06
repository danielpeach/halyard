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

package com.netflix.spinnaker.halyard.config.model.v1

import com.netflix.spinnaker.halyard.config.model.v1.node.NodeFilter
import com.netflix.spinnaker.halyard.config.model.v1.node.Providers
import com.netflix.spinnaker.halyard.config.model.v1.providers.dockerRegistry.DockerRegistryProvider
import com.netflix.spinnaker.halyard.config.model.v1.providers.google.GoogleProvider
import com.netflix.spinnaker.halyard.config.model.v1.providers.kubernetes.KubernetesProvider

class ProvidersSpec {
  void "filter matches providers"() {
    when:
    def filter = new NodeFilter().setProvider("anything")
    def providers = new Providers()

    then:
    providers.matchesToRoot(filter)
  }

  void "filter doesn't match providers"() {
    when:
    def filter = new NodeFilter().setProvider(null)
    def providers = new Providers()

    then:
    !providers.matchesToRoot(filter)
  }

  void "providers correctly reports configurable providers"() {
    setup:
    def providers = new Providers()
    def iterator = providers.getChildren()
    def kubernetes = false
    def dockerRegistry = false
    def google = false

    when:
    def child = iterator.getNext()
    while (child != null) {
      if (child instanceof KubernetesProvider) {
        kubernetes = true
      }

      if (child instanceof DockerRegistryProvider) {
        dockerRegistry = true
      }

      if (child instanceof GoogleProvider) {
        google = true
      }

      child = iterator.getNext()
    }

    then:
    kubernetes
    dockerRegistry
    google
  }
}
