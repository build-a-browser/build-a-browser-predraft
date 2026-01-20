package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;

public class TagNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeNameState);
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.selfClosingStartTagState);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case 0:
        tokenizeContext.currentTagToken().appendToName(0xFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentTagToken().appendToName(ASCIIUtil.toLower(ch));
        break;
    }
  }

}
