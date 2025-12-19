package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class BeforeHeadInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    switch (ch) {
      case '\t', '\n', '\f', '\r', ' ':
        return false;
      default:
        return handleAnythingElse(parseContext);
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
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag() && tagToken.name().equals("html")) {
      return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
    } else if (tagToken.isStartTag() && tagToken.name().equals("head")) {
      MutableElement element = ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setTheHeadElementPointer(element);
      parseContext.setInsertionMode(InsertionModes.inHeadInsertionMode);
      return false;
    } else if (!tagToken.isStartTag()) {
      switch (tagToken.name()) {
        case "head", "body", "html", "br": break;
        default: {
          parseContext.parseError();
          return false;
        }
      }
    }

    return handleAnythingElse(parseContext);
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    MutableElement element = ParseElementUtil.insertAnHTMLElement(parseContext, TagToken.create(true, "head"));
    parseContext.setTheHeadElementPointer(element);
    parseContext.setInsertionMode(InsertionModes.inHeadInsertionMode);
    return true;
  }
  
}
