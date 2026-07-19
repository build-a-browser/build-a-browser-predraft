package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import java.util.Set;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseAdjustUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InBodyInsertionMode implements InsertionMode {

  private static final Set<String> DT_SPECIAL_EXCLUSIONS = Set.of(
    "address", "div", "p");

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    switch (ch) {
      case 0:
        parseContext.parseError();
        return false;
      case '\t', '\n', '\f', '\r', ' ':
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
        ParseTextUtil.insertACharacter(parseContext, ch);
        return false;
      default:
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
        ParseTextUtil.insertACharacter(parseContext, ch);
        parseContext.setFramesetOk(false);
        return false;
    }
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
    ParseTextUtil.insertAString(parseContext, data);
    parseContext.setFramesetOk(false);
    return false;
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    ParseCommentUtil.insertAComment(parseContext, commentToken);
    return false;
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    parseContext.parseError();
    return false;
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    // TODO: Follow spec
    parseContext.stopParsing();
    return false;
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      return emitStartTagToken(parseContext, tagToken);
    } else {
      return emitEndTagToken(parseContext, tagToken);
    }
  }

  // TODO: All the other tag cases
  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "template", "title":
        return InsertionModes.IN_HEAD_INSERTION_MODE.emitTagToken(parseContext, tagToken);
      case "address", "article", "aside", "blockquote", "center", "details", "dialog", "dir", "div", "dl",
      "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "main", "menu", "nav", "ol", "p",
      "search", "section", "summary", "ul":
        if (ParseAdjustUtil.hasInButtonScope(parseContext.openElementStack(), "p")) {
          ParseAdjustUtil.closeAPElement(parseContext);
        }
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        return false;
      case "li": { // TODO: li and dt are similar, make a helper method to merge them?
        parseContext.setFramesetOk(false);
        OpenElementStack stack = parseContext.openElementStack();
        Node node = stack.peek();
        int i = 1;
        while (true) {
          if (ParseElementUtil.isHTMLElementWithName(node, "li")) {
            ParseAdjustUtil.generateImpliedEndTags(stack, Set.of("li"));
            if (!ParseElementUtil.isHTMLElementWithName(stack.peek(), "li")) {
              parseContext.parseError();
            }

            while (!ParseElementUtil.isHTMLElementWithName(stack.popNode(), "li"));
            break;
          }
          
          if (
            ParseElementUtil.isSpecial(node, DT_SPECIAL_EXCLUSIONS)
          ) break;
          node = stack.peek(i++);
        }
        
        if (ParseAdjustUtil.hasInButtonScope(parseContext.openElementStack(), "p")) {
          ParseAdjustUtil.closeAPElement(parseContext);
        }
        
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        return false;
      }
      case "dd", "dt": {
        parseContext.setFramesetOk(false);
        OpenElementStack stack = parseContext.openElementStack();
        Node node = stack.peek();
        int i = 1;
        while (true) {
          if (
            node instanceof Element element
            && (
              ParseElementUtil.isHTMLElementWithName(node, "dt")
              || ParseElementUtil.isHTMLElementWithName(node, "dd"))
          ) {
            ParseAdjustUtil.generateImpliedEndTags(stack, Set.of(element.name()));
            if (!ParseElementUtil.isHTMLElementWithName(stack.peek(), element.name())) {
              parseContext.parseError();
            }

            while (!ParseElementUtil.isHTMLElementWithName(stack.popNode(), element.name()));
            break;
          }
          
          if (
            ParseElementUtil.isSpecial(node, DT_SPECIAL_EXCLUSIONS)
          ) break;
          node = stack.peek(i++);
        }

        if (ParseAdjustUtil.hasInButtonScope(parseContext.openElementStack(), "p")) {
          ParseAdjustUtil.closeAPElement(parseContext);
        }
        
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        return false;
      }
      case "table":
        if (ParseAdjustUtil.hasInButtonScope(parseContext.openElementStack(), "p")) {
          ParseAdjustUtil.closeAPElement(parseContext);
        }
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        parseContext.setFramesetOk(false);
        parseContext.setInsertionMode(InsertionModes.IN_TABLE_INSERTION_MODE);
        return false;
      case "area", "br", "embed", "img", "keygen", "wbr":
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        parseContext.openElementStack().popNode();
        // TODO: Acknowledge self-closing flag
        parseContext.setFramesetOk(false);
        return false;
      case "input":
        // TODO: Check additional scopes
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        parseContext.openElementStack().popNode();
        // TODO: Acknowledge self-closing flag
        parseContext.setFramesetOk(false);
        return false;
      case "hr": 
        if (ParseAdjustUtil.hasInButtonScope(parseContext.openElementStack(), "p")) {
          ParseAdjustUtil.closeAPElement(parseContext);
        }
        // TODO: Handle select
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        parseContext.openElementStack().popNode();
        tagToken.acknowledgeSelfClosingFlag();
        parseContext.setFramesetOk(false);
        return false;
      default:
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        return false;
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    OpenElementStack stack = parseContext.openElementStack();
    switch (tagToken.name()) {
      case "body":
        // TODO: Other stuff
        parseContext.setInsertionMode(InsertionModes.AFTER_BODY_INSERTION_MODE);
        return false;
      case "li":
        if (!ParseAdjustUtil.hasInListItemScope(stack, "li")) {
          parseContext.parseError();
          return false;
        }
        ParseAdjustUtil.generateImpliedEndTags(stack, Set.of("li"));
        if (!ParseElementUtil.isHTMLElementWithName(stack.peek(), "li")) {
          parseContext.parseError();
        }
        while (!ParseElementUtil.isHTMLElementWithName(stack.popNode(), "li"));
        return false;
      case "dd", "dt":
        if (!ParseAdjustUtil.hasInScope(stack, tagToken.name())) {
          parseContext.parseError();
          return false;
        }
        ParseAdjustUtil.generateImpliedEndTags(stack, Set.of(tagToken.name()));
        if (!ParseElementUtil.isHTMLElementWithName(stack.peek(), tagToken.name())) {
          parseContext.parseError();
        }
        while (!ParseElementUtil.isHTMLElementWithName(stack.popNode(), tagToken.name()));
        return false;
      case "br":
        parseContext.parseError();
        return emitStartTagToken(parseContext, tagToken);
      default:
        return handleOtherEndTagToken(parseContext, tagToken);
    }
  }

  private boolean handleOtherEndTagToken(ParseContext parseContext, TagToken tagToken) {
    OpenElementStack stack = parseContext.openElementStack();
    for (int i = 0; i < stack.size(); i++) {
      Node node = stack.peek(i);
      if (ParseElementUtil.isHTMLElementWithName(node, tagToken.name())) {
        ParseAdjustUtil.generateImpliedEndTags(stack, Set.of(tagToken.name()));
        if (node != stack.peek()) {
          parseContext.parseError();
        }

        while (stack.peek() != node) {
          stack.popNode();
        }
        stack.popNode();
        return false;
      }

      // TODO: Check if node is special category
    }

    // TODO: Throw illegal state once special implemented
    return false;
  }
  
}
