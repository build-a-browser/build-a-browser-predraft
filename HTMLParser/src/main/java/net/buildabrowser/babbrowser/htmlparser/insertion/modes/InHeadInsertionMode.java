package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseTextUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InHeadInsertionMode implements InsertionMode {

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
    // TODO: More start tag cases
    switch (tagToken.name()) {
      // Include "title" in this step?
      case "html":
        return InsertionModes.inBodyInsertionMode.emitTagToken(parseContext, tagToken);
      case "title":
        ParseElementUtil.startGenericRCDataElementParsingAlgorithm(parseContext, tagToken);
        return false;
      case "noframes", "style":
        ParseElementUtil.startGenericRawTextElementParsingAlgorithm(parseContext, tagToken);
        return false;
      case "head":
        parseContext.parseError();
        return false;
      default:
        return handleAnythingElse(parseContext);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "head":
        MutableNode popped = parseContext.openElementStack().popNode();
        assert(popped instanceof MutableElement poppedElement && poppedElement.name().equals("head"));
        parseContext.setInsertionMode(InsertionModes.afterHeadInsertionMode);
        return false;
      case "body", "html", "br":
        return handleAnythingElse(parseContext);
      // TODO: Handle template
      default:
        parseContext.parseError();
        return false;
    }
  }

  private boolean handleAnythingElse(ParseContext parseContext) {
    MutableNode popped = parseContext.openElementStack().popNode();
    assert(popped instanceof MutableElement poppedElement && poppedElement.name().equals("head"));
    parseContext.setInsertionMode(InsertionModes.afterHeadInsertionMode);
    return true;
  }
  
}
