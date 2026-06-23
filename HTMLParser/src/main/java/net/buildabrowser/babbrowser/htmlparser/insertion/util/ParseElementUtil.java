package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.AnchorElement;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.html.LinkElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public final class ParseElementUtil {
  
  private ParseElementUtil() {}

  public static Element createAnElementForAToken(TagToken token, String namespace, Node intendedParent) {
    // TODO: Half the spec
    String localName = token.name();

    // TODO: Proper DOM create an element
    Element element = switch (token.name()) {
      case "a" -> AnchorElement.create(localName, intendedParent);
      case "input" -> HTMLInputElement.create(localName, intendedParent);
      case "link" -> LinkElement.create(localName, intendedParent);
      default -> HTMLElement.create(localName, intendedParent);
    };

    token.copyAttributesTo(element);

    return element;
  }

  public static Node appropriatePlaceForInsertingANode(ParseContext parseContext, Node targetOverride) {
    // TODO: Follow the spec
    return targetOverride != null ? targetOverride : parseContext.openElementStack().peek();
  }

  private static void insertAnElementAtTheAdjustedInsertionLocation(Element element, Node adjustedInsertionLocation) {
    // TODO: Follow the spec
    adjustedInsertionLocation.appendChild(element);
  }

  public static Element insertAForeignElement(ParseContext parseContext, TagToken token, String namespace, boolean onlyAddToElementStack) {
    Node adjustedInsertionLocation = appropriatePlaceForInsertingANode(parseContext, null);
    Element element = createAnElementForAToken(token, namespace, adjustedInsertionLocation);
    if (!onlyAddToElementStack) {
      insertAnElementAtTheAdjustedInsertionLocation(element, adjustedInsertionLocation);
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
    parseContext.tokenizeContext().setTokenizeState(TokenizeStates.rawTextState);
    parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
    parseContext.setInsertionMode(InsertionModes.textInsertionMode);
  }

  public static void startGenericRCDataElementParsingAlgorithm(ParseContext parseContext, TagToken tagToken) {
    assert tagToken.isStartTag();
    insertAnHTMLElement(parseContext, tagToken);
    parseContext.tokenizeContext().setTokenizeState(TokenizeStates.rcdataState);
    parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
    parseContext.setInsertionMode(InsertionModes.textInsertionMode);
  }

}
