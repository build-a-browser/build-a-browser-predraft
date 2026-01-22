package net.buildabrowser.babbrowser.htmlparser.shared.imp;

import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionMode;
import net.buildabrowser.babbrowser.htmlparser.insertion.InsertionModes;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;

public class ParseContextImp implements ParseContext {

  private final OpenElementStack openElementStack = OpenElementStack.create();

  private final MutableDocument document;
  private final TokenizeContext tokenizeContext;

  private InsertionMode currentInsertionMode;
  private InsertionMode originalInsertionMode;
  private MutableElement headElementPointer;

  private TagToken lastTagToken;

  public ParseContextImp(MutableDocument document, TokenizeContext tokenizeContext) {
    this.document = document;
    this.tokenizeContext = tokenizeContext;
    this.currentInsertionMode = InsertionModes.initialInsertionMode;
  }

  @Override
  public void emitCharacterToken(int ch) {
    boolean shouldReprocess;
    do {
      shouldReprocess = currentInsertionMode.emitCharacterToken(this, ch);
    } while (shouldReprocess);
  }

  @Override
  public void emitEOFToken() {
    boolean shouldReprocess;
    do {
      shouldReprocess = currentInsertionMode.emitEOFToken(this);
    } while (shouldReprocess);
  }

  @Override
  public void emitTagToken(TagToken tagToken) {
    this.lastTagToken = tagToken;
    boolean shouldReprocess;
    do {
      shouldReprocess = currentInsertionMode.emitTagToken(this, tagToken);
    } while (shouldReprocess);
  }

  @Override
  public void emitDoctypeToken(DoctypeToken doctypeToken) {
    boolean shouldReprocess;
    do {
      shouldReprocess = currentInsertionMode.emitDoctypeToken(this, doctypeToken);
    } while (shouldReprocess);
  }

  @Override
  public void emitCommentToken(CommentToken commentToken) {
    boolean shouldReprocess;
    do {
      shouldReprocess = currentInsertionMode.emitCommentToken(this, commentToken);
    } while (shouldReprocess);
  }

  @Override
  // TODO: This should probably be in the tokenize context, but we have access to the
  // last token here
  public boolean isAppropriateEndTagToken(TagToken tagToken) {
    return this.lastTagToken != null && lastTagToken.name().equals(tagToken.name());
  }

  @Override
  public void setInsertionMode(InsertionMode insertionMode) {
    this.currentInsertionMode = insertionMode;
  }

  @Override
  public InsertionMode currentInsertionMode() {
    return this.currentInsertionMode;
  }

  @Override
  public InsertionMode originalInsertionMode() {
    return this.originalInsertionMode;
  }

  @Override
  public void setOriginalInsertionMode(InsertionMode mode) {
    this.originalInsertionMode = mode;
  }

  @Override
  public OpenElementStack openElementStack() {
    return this.openElementStack;
  }

  @Override
  public MutableDocument document() {
    return this.document;
  }

  @Override
  public void parseError() {}

  @Override
  public void setTheHeadElementPointer(MutableElement element) {
    this.headElementPointer = element;
  }

  @Override
  public MutableElement headElementPointer() {
    return this.headElementPointer;
  }

  @Override
  public void setFramesetOk(boolean b) {
    // TODO: Implement
  }

  @Override
  public void stopParsing() {
    // Hopefully, HTMLParserImp will stop for us...
  }

  @Override
  public TokenizeContext tokenizeContext() {
    return this.tokenizeContext;
  }
  
}
