package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import java.util.Set;

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
      default:
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        return false;
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "body":
        // TODO: Other stuff
        parseContext.setInsertionMode(InsertionModes.AFTER_BODY_INSERTION_MODE);
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
