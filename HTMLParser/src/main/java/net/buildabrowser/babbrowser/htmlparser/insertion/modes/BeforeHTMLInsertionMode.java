package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class BeforeHTMLInsertionMode implements InsertionMode {

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    parseContext.parseError();
    return false;
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    ParseCommentUtil.insertAComment(parseContext, commentToken, parseContext.document());
    return false;
  }

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
  public boolean emitEOFToken(ParseContext parseContext) {
    return handleAnythingElse(parseContext);
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag() && tagToken.name().equals("html")) {
      MutableElement element = ParseElementUtil.createAnElementForAToken(tagToken, Namespace.HTML_NAMESPACE, parseContext.document());
      parseContext.document().appendChild(element);
      parseContext.openElementStack().pushNode(element);
      parseContext.setInsertionMode(InsertionModes.beforeHeadInsertionMode);

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
    MutableElement element = MutableElement.create("html", parseContext.document());
    parseContext.document().appendChild(element);
    parseContext.openElementStack().pushNode(element);

    parseContext.setInsertionMode(InsertionModes.beforeHeadInsertionMode);
    
    return true;
  }
  
}
