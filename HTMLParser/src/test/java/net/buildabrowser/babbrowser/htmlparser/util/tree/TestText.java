package net.buildabrowser.babbrowser.htmlparser.util.tree;

public record TestText(String text) implements TestNode {
  
  public static TestText testText(String text) {
    return new TestText(text);
  }

}
