package net.buildabrowser.babbrowser.htmlparser.insertion.modes;

import java.util.Set;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.InsertionModeUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseAdjustUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseCommentUtil;
import net.buildabrowser.babbrowser.htmlparser.insertion.util.ParseElementUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class InTableInsertionMode implements InsertionMode {

  // TODO: Use qualified names
  private static final Set<String> TEXT_TARGETS = Set.of(
    "table", "tbody", "template", "tfoot", "thead", "tr");

  @Override
  public boolean emitCharacterToken(ParseContext parseContext, int ch) {
    String elName = parseContext.openElementStack().peek() instanceof Element el ?
      el.name() : null;
    if (elName != null && TEXT_TARGETS.contains(elName)) {
      // TODO: Pending table character tokens
      parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
      parseContext.setInsertionMode(InsertionModes.IN_TABLE_TEXT_INSERTION_MODE);
      return true;
    } else {
      parseContext.parseError();
      parseContext.setFosterParentingEnabled(true);
      boolean reprocess = InsertionModes.IN_BODY_INSERTION_MODE.emitCharacterToken(parseContext, ch);
      parseContext.setFosterParentingEnabled(false);
      return reprocess;
    }
  }

  @Override
  public boolean emitOptimizedString(ParseContext parseContext, String data) {
    String elName = parseContext.openElementStack().peek() instanceof Element el ?
      el.name() : null;
    if (elName != null && TEXT_TARGETS.contains(elName)) {
      // TODO: Pending table character tokens
      parseContext.setOriginalInsertionMode(parseContext.currentInsertionMode());
      parseContext.setInsertionMode(InsertionModes.IN_TABLE_TEXT_INSERTION_MODE);
      return true;
    } else {
      parseContext.parseError();
      parseContext.setFosterParentingEnabled(true);
      boolean reprocess = InsertionModes.IN_BODY_INSERTION_MODE.emitOptimizedString(parseContext, data);
      parseContext.setFosterParentingEnabled(false);
      return reprocess;
    }
  }

  @Override
  public boolean emitDoctypeToken(ParseContext parseContext, DoctypeToken doctypeToken) {
    parseContext.parseError();
    return false;
  }

  @Override
  public boolean emitCommentToken(ParseContext parseContext, CommentToken commentToken) {
    ParseCommentUtil.insertAComment(parseContext, commentToken);
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

  @Override
  public boolean emitEOFToken(ParseContext parseContext) {
    InsertionModes.IN_BODY_INSERTION_MODE.emitEOFToken(parseContext);
    return false;
  }

  private boolean emitStartTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "caption":
      clearStackBackToTableContext(parseContext);
      // TODO: Insert a marker
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setInsertionMode(InsertionModes.IN_CAPTION_INSERTION_MODE);
      return false;
    case "colgroup":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setInsertionMode(InsertionModes.IN_COLUMN_GROUP_INSERTION_MODE);
      return false;
    case "col":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext,
        TagToken.create(true, "colgroup"));
      parseContext.setInsertionMode(InsertionModes.IN_COLUMN_GROUP_INSERTION_MODE);
      return true;
    case "tbody", "tfoot", "thead":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.setInsertionMode(InsertionModes.IN_TABLE_BODY_INSERTION_MODE);
      return false;
    case "td", "th", "tr":
      clearStackBackToTableContext(parseContext);
      ParseElementUtil.insertAnHTMLElement(parseContext,
        TagToken.create(true, "tbody"));
      parseContext.setInsertionMode(InsertionModes.IN_TABLE_BODY_INSERTION_MODE);
      return true;
    case "table":
      parseContext.parseError();
      boolean isTableInTable = ParseAdjustUtil.hasInTableScope(
        parseContext.openElementStack(), "table");
      if (isTableInTable) {
        ParseAdjustUtil.popUntil(parseContext.openElementStack(), "table");
        InsertionModeUtil.resetInsertionModeAppropriately(parseContext);
        return true;
      }
      return false;
    case "style", "script", "template":
      InsertionModes.IN_HEAD_INSERTION_MODE.emitTagToken(parseContext, tagToken);
      return false;
    case "input":
      String typeAttr = tagToken.attribute("type");
      if (
        typeAttr == null
        || !typeAttr.equalsIgnoreCase("hidden")
      ) {
        return handleAnythingElseTag(parseContext, tagToken);
      }

      parseContext.parseError();
      ParseElementUtil.insertAnHTMLElement(parseContext, tagToken);
      parseContext.openElementStack().popNode();
      if (tagToken.isSelfClosing()) {
        tagToken.acknowledgeSelfClosingFlag();
      }
      return false;
    default:
      return handleAnythingElseTag(parseContext, tagToken);
    }
  }

  private boolean emitEndTagToken(ParseContext parseContext, TagToken tagToken) {
    switch (tagToken.name()) {
    case "table":
      boolean isInTable = ParseAdjustUtil.hasInTableScope(
      parseContext.openElementStack(), "table");
      if (!isInTable) {
        parseContext.parseError();
        return false;
      }

      ParseAdjustUtil.popUntil(parseContext.openElementStack(), "table");
      InsertionModeUtil.resetInsertionModeAppropriately(parseContext);
      return false;
    case "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr":
      parseContext.parseError();
      return false;
    case "template":
      InsertionModes.IN_HEAD_INSERTION_MODE.emitTagToken(parseContext, tagToken);
      return false;
    default:
      return handleAnythingElseTag(parseContext, tagToken);
    }
  }

  private boolean handleAnythingElseTag(ParseContext parseContext, TagToken tagToken) {
    parseContext.parseError();
    parseContext.setFosterParentingEnabled(true);
    boolean reprocess = InsertionModes.IN_BODY_INSERTION_MODE.emitTagToken(parseContext, tagToken);
    parseContext.setFosterParentingEnabled(false);
    return reprocess;
  }

  private void clearStackBackToTableContext(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();
    while (!(
      stack.peek() instanceof HTMLElement element
      && (
        element.name().equals("table")
        || element.name().equals("template")
        || element.name().equals("html"))
    )) {
      stack.popNode();
    }
  }

}
