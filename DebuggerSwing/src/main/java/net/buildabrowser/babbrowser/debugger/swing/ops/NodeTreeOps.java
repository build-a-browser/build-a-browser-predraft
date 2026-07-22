package net.buildabrowser.babbrowser.debugger.swing.ops;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree.TreeOps;
import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public class NodeTreeOps implements TreeOps<Node> {

  @Override
  public String name(Node node) {
    if (node == null) return "NULL";
    return switch (node) {
      case Document _ -> "Document";
      case Element element -> formatElement(element); // TODO: Include ID
      case Text text -> '"' + text.data() + '"'; // TODO: Truncate
      case Comment comment -> "<!--" + comment.data() + "-->";
      default -> "<???>";
    };
  }

  @Override
  public List<Node> children(Node node) {
    List<Node> children = new ArrayList<>();
    for (Node child: node.childNodes()) {
      children.add(child);
    }

    return children;
  }

  @Override
  public boolean isNodeIgnored(Node node) {
    return node instanceof Text text && text.data().isBlank();
  }

  @Override
  public boolean isNodeLeaf(Node node) {
    return node instanceof Text || node instanceof Comment;
  }

  private String formatElement(Element element) {
    StringBuilder elementBuilder = new StringBuilder()
      .append('<')
      .append(element.name());
    for (String attrName: element.getAttributeNames()) {
      elementBuilder
        .append(' ')
        .append(attrName);
      String attrValue = element.getAttribute(attrName);
      if (!attrValue.isEmpty()) {
        // TODO: Escape value
        elementBuilder
          .append("=\"")
          .append(attrValue)
          .append('"');
      }
    }

    elementBuilder.append('>');
    return elementBuilder.toString();
  }
  
}
