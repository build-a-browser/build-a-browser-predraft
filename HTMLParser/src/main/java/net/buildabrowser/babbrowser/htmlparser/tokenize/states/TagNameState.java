package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class TagNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_ATTRIBUTE_NAME_STATE);
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.SELF_CLOSING_START_TAG_STATE);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
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
