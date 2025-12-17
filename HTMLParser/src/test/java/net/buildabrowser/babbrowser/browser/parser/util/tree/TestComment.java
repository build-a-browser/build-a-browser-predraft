package net.buildabrowser.babbrowser.browser.parser.util.tree;

public record TestComment(String data) implements TestNode {
  
  public static TestComment testComment(String data) {
    return new TestComment(data);
  }

}
