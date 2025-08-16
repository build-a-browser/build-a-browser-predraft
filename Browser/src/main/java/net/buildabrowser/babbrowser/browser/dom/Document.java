package net.buildabrowser.babbrowser.browser.dom;

import java.util.List;

public record Document(List<Node> children) implements Node {
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Node child: children) {
      builder.append(child.toString());
    }
    
    return builder.toString();
  }

}
