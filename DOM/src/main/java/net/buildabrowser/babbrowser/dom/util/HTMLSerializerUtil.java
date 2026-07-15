package net.buildabrowser.babbrowser.dom.util;

import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public final class HTMLSerializerUtil {
  
  private HTMLSerializerUtil() {}

  public static String serializeNode(Node node) {
    return switch (node) {
      case CustomNodeSerializer serializer -> serializer.serialize();
      case Document document -> serializeGeneric(document);
      case Element element -> serializeElement(element);
      case Comment comment -> serializeComment(comment);
      case Text text -> serializeText(text);
      default -> serializeGeneric(node);
    };
  }

  // TODO: Need to exclude collapsed whitespace
  public static String serializeNodeAsText(Node node) {
    StringBuilder stringBuilder = new StringBuilder();
    serializeNodeAsText(node, stringBuilder);
    return stringBuilder.toString();
  }

  private static void serializeNodeAsText(Node node, StringBuilder stringBuilder) {
    if (node instanceof Text text) {
      stringBuilder.append(text.data());
    }
    node.forEachChild(child -> {
      serializeNodeAsText(child, stringBuilder);
    });
  }

  private static String serializeComment(Comment comment) {
    return "<!--" + sanitizeText(comment.data().toString()) + "-->";
  }

  private static String serializeGeneric(Node container) {
    StringBuilder builder = new StringBuilder();
    container.forEachChild(child -> {
      builder.append(serializeNode(child));
    });
    
    return builder.toString();
  }

  // TODO: This does not properly sanitize RAWTEXT, RCDATA, or script elements
  public static String serializeElement(Element element) {
    String name = element.name();

    StringBuilder builder = new StringBuilder("<");
    builder.append(name);

    for (String attributeName: element.getAttributeNames()) {
      builder.append(' ');
      builder.append(attributeName);
      builder.append("=\"");
      builder.append(sanitizeAttributeValue(element.getAttribute(attributeName)));
      builder.append('"');
    }
    builder.append(">");
    element.forEachChild(child -> {
      builder.append(serializeNode(child));
    });
    builder
      .append("</")
      .append(name)
      .append(">");
    
    return builder.toString();
  }

  private static String serializeText(Text text) {
    return sanitizeText(text.data());
  }

  private static String sanitizeAttributeValue(String attribute) {
    return attribute.replace("\"", "&quot;");
  }

  private static String sanitizeText(String text) {
    return text
      .replace("<", "&lt;")
      .replace(">", "&gt;");
  }

  public static interface CustomNodeSerializer {
  
    String serialize();

  }

}
