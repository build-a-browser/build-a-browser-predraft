package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AttributeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '\t', '\n', '\f', ' ', '/', '>', TokenizeContext.EOF:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.afterAttributeNameState);
        break;
      case '=':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeValueState);
        break;
      case 0:
        tokenizeContext.currentTagToken().appendToAttributeName(0xFFFD);
        break;
      case '"', '\'', '<':
        parseContext.parseError();
        tokenizeContext.currentTagToken().appendToAttributeName(ch);
        break;
      default:
        tokenizeContext.currentTagToken().appendToAttributeName(ASCIIUtil.toLower(ch));
        break;
    }
  }

}
