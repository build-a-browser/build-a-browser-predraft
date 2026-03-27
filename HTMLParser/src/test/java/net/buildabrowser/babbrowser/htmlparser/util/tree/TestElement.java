package net.buildabrowser.babbrowser.htmlparser.util.tree;

import java.util.List;
import java.util.Map;

public record TestElement(String name, Map<String, String> attributes, List<TestNode> children) implements TestNode {
  
  public static TestElement testElement(String name, TestNode... children) {
    return new TestElement(name, Map.of(), List.of(children));
  }

  public static TestElement testElement(String name, Map<String, String> attributes, TestNode... children) {
    return new TestElement(name, attributes, List.of(children));
  }

}
