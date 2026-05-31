package net.buildabrowser.babbrowser.htmlparser.util.tree;

import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestDocument.testDocument;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestElement.testElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.NodeList;
import net.buildabrowser.babbrowser.dom.Text;

public final class TestUtil {

  private TestUtil() {}

  public static TestDocument testDocumentToBody(TestNode... children) {
    return testDocument(
      testElement("html",
        testElement("head"),
        testElement("body", children)));
  }

  public static TestDocument testDocumentToHead(TestNode... children) {
    return testDocument(
      testElement("html",
        testElement("head", children),
        testElement("body")));
  }
  
  public static void assertTreeMatches(TestNode reference, Node node) {
    switch (reference) {
      case TestDocument document -> assertDocumentMatches(document, node);
      case TestElement element -> assertElementMatches(element, node);
      case TestText text -> assertTextMatches(text, node);
      case TestComment comment -> assertCommentMatches(comment, node);
      default -> throw new AssertionError("Unrecognize reference node type");
    }
  }

  private static void assertDocumentMatches(TestDocument refDocument, Node node) {
    if (!(node instanceof Document document)) {
      throwDivergentTypes(refDocument, node);
      return;
    }

    assertNodeListMatches(refDocument.children(), document.childNodes());
  }

  private static void assertElementMatches(TestElement refElement, Node node) {
    if (!(node instanceof Element element)) {
      throwDivergentTypes(refElement, node);
      return;
    }

    if (!refElement.name().equals(element.name())) {
      throw new AssertionError(String.format(
        "Reference and actual names diverged: Expected %s, got %s",
        refElement.name(),
        element.name()));
    }

    assertAttributesListMatches(refElement.attributes(), element);
    assertNodeListMatches(refElement.children(), element.childNodes());
  }

  private static void assertTextMatches(TestText refText, Node node) {
    if (!(node instanceof Text text)) {
      throwDivergentTypes(refText, node);
      return;
    }

    Assertions.assertEquals(refText.text(), text.data());
  }

  private static void assertCommentMatches(TestComment refComment, Node node) {
    if (!(node instanceof Comment comment)) {
      throwDivergentTypes(refComment, node);
      return;
    }

    Assertions.assertEquals(refComment.data(), comment.data());
  }

  private static void assertAttributesListMatches(Map<String, String> attributes, Element element) {
    Map<String, String> elAttributes = new HashMap<>();
    for (String attrName: element.getAttributeNames()) {
      elAttributes.put(attrName, element.getAttribute(attrName));
    }

    Assertions.assertEquals(attributes, elAttributes);
  }

  private static void assertNodeListMatches(List<TestNode> children, NodeList childNodes) {
    if (children.size() != childNodes.length()) {
      throw new AssertionError(String.format(
        "Reference and actual lengths diverged: Expected %s, got %s",
        children.size(),
        childNodes.length()));
    }

    for (int i = 0; i < children.size(); i++) {
      assertTreeMatches(children.get(i), childNodes.item(i));
    }
  }

  private static void throwDivergentTypes(TestNode refText, Node node) {
    throw new AssertionError(String.format(
      "Reference and actual types diverged: Expected %s, got %s",
      classifyType(refText),
      classifyType(node)));
  }
 
  private static String classifyType(Object object) {
    return switch (object) {
      case Document _1 -> "Document";
      case TestDocument _1 -> "Document";
      case Element _1 -> "Element";
      case TestElement _1 -> "Element";
      case Text _1 -> "Text";
      case TestText _1 -> "Text";
      case Comment _1 -> "Comment";
      case TestComment _1 -> "Comment";
      default -> "Unknown";
    };
  }

}
