package net.buildabrowser.babbrowser.browser.dom;

import java.util.List;

public record Element(String name, List<Node> children) implements Node {
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("<");
    builder.append(name);
    builder.append(">");
    for (Node child: children) {
      builder.append(child.toString());
    }
    builder
      .append("</")
      .append(name)
      .append(">");
    
    return builder.toString();
  }

}
