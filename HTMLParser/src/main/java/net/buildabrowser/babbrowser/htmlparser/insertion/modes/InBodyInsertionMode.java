package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
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
        return InsertionModes.inHeadInsertionMode.emitTagToken(parseContext, tagToken);
      case "area", "br", "embed", "img", "keygen", "wbr":
        ParseTextUtil.reconstructTheActiveFormattingElements(parseContext);
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
        parseContext.setInsertionMode(InsertionModes.afterBodyInsertionMode);
      default:
        return handleOtherEndTagToken(parseContext, tagToken);
    }
  }

  private boolean handleOtherEndTagToken(ParseContext parseContext, TagToken tagToken) {
    OpenElementStack stack = parseContext.openElementStack();
    for (int i = 0; i < stack.size(); i++) {
      MutableNode node = stack.peek(i);
      if (ParseElementUtil.isHTMLElementWithName(node, tagToken.name())) {
        // TODO: Generate implied end tags
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
