package net.buildabrowser.babbrowser.htmlparser.util.tree;

public record TestComment(String data) implements TestNode {
  
  public static TestComment testComment(String data) {
    return new TestComment(data);
  }

}
