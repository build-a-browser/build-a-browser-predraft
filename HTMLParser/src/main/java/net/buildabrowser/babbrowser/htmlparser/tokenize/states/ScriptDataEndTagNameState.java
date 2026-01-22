package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.TokenizeUtil;

public class ScriptDataEndTagNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (parseContext.isAppropriateEndTagToken(tokenizeContext.currentTagToken())) {
      switch (ch) {
        case '\t', '\n', '\f', ' ':
          tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeNameState);
          return;
        case '/':
          tokenizeContext.setTokenizeState(TokenizeStates.selfClosingStartTagState);
          return;
        case '>':
          tokenizeContext.setTokenizeState(TokenizeStates.dataState);
          parseContext.emitTagToken(tokenizeContext.currentTagToken());
          return;
        default:
          break;
      }
    }

    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.currentTagToken().appendToName(ASCIIUtil.toLower(ch));
      tokenizeContext.temporaryBuffer().append(ch);
      return;
    }

    parseContext.emitCharacterToken('<');
    parseContext.emitCharacterToken('/');
    TokenizeUtil.emitTemporaryBuffer(tokenizeContext, parseContext);
    tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataState);
  }
  
}
