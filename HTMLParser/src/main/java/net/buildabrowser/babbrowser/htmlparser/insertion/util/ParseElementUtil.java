package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public final class ParseElementUtil {
  
  private ParseElementUtil() {}

  public static MutableElement createAnElementForAToken(TagToken token, String namespace, MutableNode intendedParent) {
    // TODO: Half the spec
    String localName = token.name();

    // TODO: Proper DOM create an element
    MutableElement element = MutableElement.create(localName, intendedParent);

    token.attributes().forEach((k, v) -> element.addAttribute(k, v));

    return element;
  }

  public static MutableNode appropriatePlaceForInsertingANode(ParseContext parseContext, MutableNode targetOverride) {
    // TODO: Follow the spec
    return targetOverride != null ? targetOverride : parseContext.openElementStack().peek();
  }

  private static void insertAnElementAtTheAdjustedInsertionLocation(MutableElement element, MutableNode adjustedInsertionLocation) {
    // TODO: Follow the spec
    adjustedInsertionLocation.appendChild(element);
  }

  public static MutableElement insertAForeignElement(ParseContext parseContext, TagToken token, String namespace, boolean onlyAddToElementStack) {
    MutableNode adjustedInsertionLocation = appropriatePlaceForInsertingANode(parseContext, null);
    MutableElement element = createAnElementForAToken(token, namespace, adjustedInsertionLocation);
    if (!onlyAddToElementStack) {
      insertAnElementAtTheAdjustedInsertionLocation(element, adjustedInsertionLocation);
    }
    parseContext.openElementStack().pushNode(element);

    return element;
  }
  
  public static MutableElement insertAnHTMLElement(ParseContext parseContext, TagToken token) {
    return insertAForeignElement(parseContext, token, Namespace.HTML_NAMESPACE, false);
  }

  public static boolean isHTMLElementWithName(MutableNode node, String name) {
    return
      node instanceof MutableElement element
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
