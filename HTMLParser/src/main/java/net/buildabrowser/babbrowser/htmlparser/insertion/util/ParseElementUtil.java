package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.AnchorElement;
import net.buildabrowser.babbrowser.html.html.HTMLButtonElement;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.html.LinkElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.infra.Namespace;

public final class ParseElementUtil {

  private static final List<String> TABLE_ELEMENTS = List.of(
    "table", "tbody", "tfoot", "thead", "tr");
  private static final Set<String> SPECIAL_HTML = Set.of(
    "address", "applet", "area", "article", "aside", "base",
    "basefont", "bgsound", "blockquote", "body", "br", "button", "caption",
    "center", "col", "colgroup", "dd", "details", "dir", "div", "dl", "dt",
    "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame",
    "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header",
    "hgroup", "hr", "html", "iframe", "img", "input", "keygen", "li",
    "link", "listing", "main", "marquee", "menu", "meta", "nav", "noembed",
    "noframes", "noscript", "object", "ol", "p", "param", "plaintext",
    "pre", "script", "search", "section", "select", "source", "style",
    "summary", "table", "tbody", "td", "template", "textarea", "tfoot",
    "th", "thead", "title", "tr", "track", "ul", "wbr", "xmp");
  
  private ParseElementUtil() {}

  public static Element createAnElementForAToken(TagToken token, String namespace, Node intendedParent) {
    // TODO: Half the spec
    String localName = token.name();

    // TODO: Proper DOM create an element
    Element element = switch (token.name()) {
      case "a" -> AnchorElement.create(localName, intendedParent);
      case "button" -> HTMLButtonElement.create(localName, intendedParent);
      case "form" -> HTMLFormElement.create(localName, intendedParent);
      case "input" -> HTMLInputElement.create(localName, intendedParent);
      case "link" -> LinkElement.create(localName, intendedParent);
      default -> HTMLElement.create(localName, intendedParent);
    };

    token.copyAttributesTo(element);

    return element;
  }

  public static AdjustedInsertionLocation appropriatePlaceForInsertingANode(
    ParseContext parseContext, Node targetOverride
  ) {
    // TODO: Handle templates
    Node target = targetOverride != null ? targetOverride : parseContext.openElementStack().peek();
    AdjustedInsertionLocation adjustedInsertionLocation;
    if (
      parseContext.fosterParentingEnabled()
      && target instanceof HTMLElement element
      && element.namespace().equals(Namespace.HTML_NAMESPACE)
      && TABLE_ELEMENTS.contains(element.name())
    ) {
      OpenElementStack elementStack = parseContext.openElementStack();
      Node lastTable = lastHTMLNamed(elementStack, "table");
      if (lastTable == null) {
        Node selectedNode = elementStack.peek(elementStack.size() - 1);
        adjustedInsertionLocation = new AdjustedInsertionLocation(
          selectedNode, selectedNode.lastChild());
      } else if (lastTable.parentNode() != null) {
        adjustedInsertionLocation = new AdjustedInsertionLocation(
          lastTable.parentNode(), lastTable.previousSibling());
      } else {
        Node previousElement = beforeLastHTMLNamed(elementStack, "table");
        return new AdjustedInsertionLocation(
          previousElement, previousElement.lastChild());
      }
    } else {
      adjustedInsertionLocation = new AdjustedInsertionLocation(target, target.lastChild());
    }

    return adjustedInsertionLocation;
  }

  // TODO: Avoid the allocations of this
  public static record AdjustedInsertionLocation(
    Node parentNode, Node afterNode
  ) {}

  private static void insertAnElementAtTheAdjustedInsertionLocation(
    ParseContext parseContext, Element element
  ) {
    // TODO: Follow the spec
    AdjustedInsertionLocation adjustedInsertionLocation = appropriatePlaceForInsertingANode(
      parseContext, null);
    insertNodeAt(element, adjustedInsertionLocation);
  }

  public static void insertNodeAt(
    Node node, AdjustedInsertionLocation adjustedInsertionLocation
  ) {
    Node prevNode = adjustedInsertionLocation.afterNode();
    adjustedInsertionLocation.parentNode().insertBefore(
      node, prevNode == null ? null : prevNode.nextSibling());
  }

  public static Element insertAForeignElement(
    ParseContext parseContext, TagToken token,
    String namespace, boolean onlyAddToElementStack
  ) {
    AdjustedInsertionLocation adjustedInsertionLocation = appropriatePlaceForInsertingANode(
      parseContext, null);
    Element element = createAnElementForAToken(
      token, namespace, adjustedInsertionLocation.parentNode());
    if (!onlyAddToElementStack) {
      insertAnElementAtTheAdjustedInsertionLocation(parseContext, element);
    }
    parseContext.openElementStack().pushNode(element);

    return element;
  }
  
  public static Element insertAnHTMLElement(ParseContext parseContext, TagToken token) {
    return insertAForeignElement(parseContext, token, Namespace.HTML_NAMESPACE, false);
  }

  public static boolean isHTMLElementWithName(Node node, String name) {
    return
      node instanceof Element element
      && element.name().equals(name)
      && element.namespace().equals(Namespace.HTML_NAMESPACE);
  }

  public static void startGenericRawTextElementParsingAlgorithm(ParseContext parseContext, TagToken tagToken) {
    assert tagToken.isStartTag();
    insertAnHTMLElement(parseContext, tagToken);
    parseContext.tokenizeContext().setTokenizeState(TokenizeStates.RAW_TEXT_STATE);
    parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
    parseContext.setInsertionMode(InsertionModes.TEXT_INSERTION_MODE);
  }

  public static void startGenericRCDataElementParsingAlgorithm(ParseContext parseContext, TagToken tagToken) {
    assert tagToken.isStartTag();
    insertAnHTMLElement(parseContext, tagToken);
    parseContext.tokenizeContext().setTokenizeState(TokenizeStates.RCDATA_STATE);
    parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
    parseContext.setInsertionMode(InsertionModes.TEXT_INSERTION_MODE);
  }
  
  public static boolean isSpecial(
    Node node, Set<String> exceptions
  ) {
    // TODO: Other namespaces
    if (!(node instanceof Element element)) return false;
    if (!element.namespace().equals(Namespace.HTML_NAMESPACE)) return false;
    if (exceptions.contains(element.name())) return false;
    return SPECIAL_HTML.contains(element.name());
  }

  private static Node lastHTMLNamed(
    OpenElementStack elementStack, String name
  ) {
    Iterator<Node> stackIt = elementStack.iterator();
    while (stackIt.hasNext()) {
      Node node = stackIt.next();
      if (isHTMLNamed(node, name)) {
        return node;
      }
    }

    return null;
  }

  // TODO: Avoid scanning the stack twice
  private static Node beforeLastHTMLNamed(
    OpenElementStack elementStack, String name
  ) {
    Iterator<Node> stackIt = elementStack.iterator();
    while (stackIt.hasNext()) {
      Node node = stackIt.next();
      if (isHTMLNamed(node, name)) {
        return stackIt.next();
      }
    }

    return null;
  }

  private static boolean isHTMLNamed(Node node, String name) {
    return 
      node instanceof HTMLElement element
      && element.namespace().equals(Namespace.HTML_NAMESPACE)
      && element.name().equals(name);
  }

}
