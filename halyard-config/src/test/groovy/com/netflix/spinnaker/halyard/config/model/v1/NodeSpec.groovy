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

import com.netflix.spinnaker.halyard.config.model.v1.node.*
import com.netflix.spinnaker.halyard.config.model.v1.problem.ProblemSetBuilder
import spock.lang.Specification

class NodeSpec extends Specification {
  static final List<String> field1Options = ["a", "b", "c"]

  class TestNode extends Node {
    List<String> field1Options(ProblemSetBuilder p) {
      return field1Options
    }

    ChildTestNode node1 = new ChildTestNode()
    ChildTestNode node2 = new ChildTestNode()
    ChildTestNode node3 = new ChildTestNode()

    @LocalFile String file1 = "/a/b/c/"
    @LocalFile String file2 = "/d/e/f/"

    @Override
    void accept(ProblemSetBuilder psBuilder, Validator v) {

    }

    @Override
    String getNodeName() {
      return "test"
    }

    @Override
    NodeIterator getChildren() {
      return NodeIteratorFactory.makeReflectiveIterator(this)
    }

    @Override
    protected boolean matchesLocally(NodeFilter filter) {
      return false
    }

    @Override
    NodeFilter getFilter() {
      return null
    }
  }

  class ChildTestNode extends Node {
    @Override
    void accept(ProblemSetBuilder psBuilder, Validator v) {

    }

    List<Node> childNodes = new ArrayList<>();

    @Override
    String getNodeName() {
      return "childtest"
    }

    @Override
    NodeIterator getChildren() {
      return NodeIteratorFactory.makeListIterator(childNodes)
    }

    @Override
    protected boolean matchesLocally(NodeFilter filter) {
      return true
    }

    @Override
    NodeFilter getFilter() {
      return null
    }
  }

  void "node reports matching field options"() {
    setup:
    def node = new TestNode()

    when:
    def options = node.fieldOptions(null, "field1")

    then:
    options == field1Options
  }

  void "node reports no matching field options"() {
    setup:
    def node = new TestNode()

    when:
    def options = node.fieldOptions(null, "field2")

    then:
    options == []
  }

  void "node correctly provides reflective iterator"() {
    setup:
    def node = new TestNode()
    def iterator = node.getChildren()

    when:
    def child = iterator.getNext()

    then:
    child != null
    while (child != null) {
      child.nodeName == new ChildTestNode().getNodeName()
      child = iterator.getNext()
    }
  }

  void "node correctly provides list iterator"() {
    setup:
    def node = new ChildTestNode()
    node.childNodes = [new ChildTestNode(), new ChildTestNode(), new ChildTestNode()]
    def iterator = node.getChildren()

    when:
    def child = iterator.getNext()
    def i = 0

    then:
    child != null
    while (child != null) {
      i++
      child = iterator.getNext()
    }

    i == node.childNodes.size()
  }

  void "node correctly reports localfiles"() {
    setup:
    def node = new TestNode()

    when:
    def files = node.localFiles()

    then:
    files.size() == 2
  }

  void "node correctly reports no localfiles"() {
    setup:
    def node = new ChildTestNode()

    when:
    def files = node.localFiles()

    then:
    files.size() == 0
  }
}
