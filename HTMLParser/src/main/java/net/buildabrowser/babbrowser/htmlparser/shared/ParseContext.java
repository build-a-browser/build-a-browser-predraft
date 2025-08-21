package net.buildabrowser.babbrowser.htmlparser.shared;

import net.buildabrowser.babbrowser.htmlparser.shared.imp.ParseContextImp;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.spec.dom.Document;

public interface ParseContext {

  void emitCharacterToken(int ch);

  void emitEOFToken();

  void emitTagToken(TagToken tagToken);

  public static ParseContext create(Document document) {
    return new ParseContextImp(document);
  }
  
}
