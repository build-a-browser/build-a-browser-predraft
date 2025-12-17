package net.buildabrowser.babbrowser.browser.parser.util.tree;

import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestDocument.testDocument;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestElement.testElement;

import java.util.List;

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

    Assertions.assertEquals(refElement.attributes(), element.attributes());
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
      case Document _ -> "Document";
      case TestDocument _ -> "Document";
      case Element _ -> "Element";
      case TestElement _ -> "Element";
      case Text _ -> "Text";
      case TestText _ -> "Text";
      case Comment _ -> "Comment";
      case TestComment _ -> "Comment";
      default -> "Unknown";
    };
  }

}
