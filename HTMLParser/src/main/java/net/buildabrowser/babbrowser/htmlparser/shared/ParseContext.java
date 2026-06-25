package net.buildabrowser.babbrowser.htmlparser.shared;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.imp.ParseContextImp;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;

public interface ParseContext {

  void emitCharacterToken(int ch);

  void emitOptimizedString(String data);

  void emitEOFToken();

  void emitTagToken(TagToken tagToken);

  void emitDoctypeToken(DoctypeToken doctypeToken);

  void emitCommentToken(CommentToken commentToken);

  boolean isAppropriateEndTagToken(TagToken tagToken);

  void setInsertionMode(InsertionMode beforeHTMLInsertionMode);

  InsertionMode currentInsertionMode();

  InsertionMode originalInsertionMode();

  void setOriginalInsertionMode(InsertionMode mode);

  OpenElementStack openElementStack();

  Document document();

  // TODO: Accept enum type
  void parseError();

  void setTheHeadElementPointer(Element element);

  Element headElementPointer();

  void setFramesetOk(boolean b);

  void setFosterParentingEnabled(boolean b);

  boolean fosterParentingEnabled();

  void stopParsing();

  TokenizeContext tokenizeContext();

  public static ParseContext create(Document document, TokenizeContext tokenizeContext) {
    return new ParseContextImp(document, tokenizeContext);
  }
  
}
