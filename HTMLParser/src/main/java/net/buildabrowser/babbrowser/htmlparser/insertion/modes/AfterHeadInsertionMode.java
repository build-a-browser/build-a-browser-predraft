package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class AfterHeadInsertionMode implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    switch (ch) {
      case '\t', '\n', '\f', '\r', ' ':
        ParseTextUtil.insertACharacter(parseContext, ch);
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
    if (tagToken.isStartTag()) {
      return emitStartTagToken(parseContext, tagToken);
    } else {
      return emitEndTagToken(parseContext, tagToken);
    }
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "title", "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "template":
        parseContext.parseError();
        parseContext.openElementStack().pushNode(parseContext.headElementPointer());
        InsertionModes.inHeadInsertionMode.emitTagToken(parseContext, tagToken);
        parseContext.openElementStack().removeSpecificNode(parseContext.headElementPointer());
        return false;
      case "html":
        return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
      case "body":
        ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
        parseContext.setFramesetOk(false);
        parseContext.setInsertionMode(InsertionModes.inBodyInsertionMode);
        return false;
      // TODO: Frameset
      // TODO: Many others
      case "head":
        parseContext.parseError();
        return false;
      default:
        return handleAnythingElse(parseContext);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "template":
        return InsertionModes.inHeadInsertionMode.emitTagToken(parseContext, tagToken);
      case "body", "html", "br":
        return handleAnythingElse(parseContext);
      default:
        parseContext.parseError();
        return false;
    }
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    ParseElementUtil.insertAnHTMLElement(parseContext, TagToken.create(true, "body"));
    parseContext.setInsertionMode(InsertionModes.inBodyInsertionMode);
    return true;
  }
  
}
