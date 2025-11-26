package net.buildabrowser.babbrowser.dom.imp;

import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;

public record DocumentImp(List<Node> children, StyleSheetList styleSheets) implements Document {
  
  /*@Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Node child: children) {
      builder.append(child.toString());
    }
    
    return builder.toString();
  }*/

}
