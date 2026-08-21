package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.InsertionModeUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseAdjustUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InTemplateInsertionMode  implements InsertionMode {

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitCharacterToken(parseContext, ch);
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitOptimizedString(parseContext, data);
  }

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    // TODO: Fragment case
    ParseAdjustUtil.popUntil(parseContext.openElementStack(), "template");
    // TODO: Clear active formatting elements
    parseContext.templateInsertionModes().pop();
    InsertionModeUtil.resetInsertionModeAppropriately(parseContext);
    return true;
  }

  @Override
  public boolean emitTagToken(ParseContext parseContext, TagToken tagToken) {
    if (tagToken.isStartTag()) {
      return emitStartTagToken(parseContext, tagToken);
    } else {
      return emitEndTagToken(parseContext, tagToken);
    }
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitDoctypeToken(parseContext, doctypeToken);
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    return InsertionModes.IN_BODY_INSERTION_MODE.emitCommentToken(parseContext, commentToken);
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "base", "basefont", "bgsound", "link", "meta", "noframes", "script",
      "style", "template", "title":
        return InsertionModes.IN_BODY_INSERTION_MODE.emitTagToken(parseContext, tagToken);
      case "caption", "colgroup", "tbody", "tfoot", "thead":
        parseContext.templateInsertionModes().pop();
        parseContext.templateInsertionModes().push(InsertionModes.IN_TABLE_INSERTION_MODE);
        parseContext.setInsertionMode(InsertionModes.IN_TABLE_INSERTION_MODE);
        return true;
      case "col":
        parseContext.templateInsertionModes().pop();
        parseContext.templateInsertionModes().push(InsertionModes.IN_COLUMN_GROUP_INSERTION_MODE);
        parseContext.setInsertionMode(InsertionModes.IN_COLUMN_GROUP_INSERTION_MODE);
        return true;
      case "tr":
        parseContext.templateInsertionModes().pop();
        parseContext.templateInsertionModes().push(InsertionModes.IN_TABLE_BODY_INSERTION_MODE);
        parseContext.setInsertionMode(InsertionModes.IN_TABLE_BODY_INSERTION_MODE);
        return true;
      case "td", "th":
        parseContext.templateInsertionModes().pop();
        parseContext.templateInsertionModes().push(InsertionModes.IN_ROW_INSERTION_MODE);
        parseContext.setInsertionMode(InsertionModes.IN_ROW_INSERTION_MODE);
        return true;
      default:
        parseContext.templateInsertionModes().pop();
        parseContext.templateInsertionModes().push(InsertionModes.IN_BODY_INSERTION_MODE);
        parseContext.setInsertionMode(InsertionModes.IN_BODY_INSERTION_MODE);
        return true;
    }
  }
  
  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
      case "template":
        return InsertionModes.IN_BODY_INSERTION_MODE.emitTagToken(parseContext, tagToken);
      default:
        parseContext.parseError();
        return false;
    }
  }
  
}
