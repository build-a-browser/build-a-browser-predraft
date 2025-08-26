package net.buildabrowser.babbrowser.css.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSRule;
import net.buildabrowser.babbrowser.css.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.css.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.css.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.css.tokens.EOFToken;
import net.buildabrowser.babbrowser.css.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.Token;
import net.buildabrowser.babbrowser.css.tokens.WhitespaceToken;

public class CSSIntermediateParserImp {
  
  public List<CSSRule> consumeAListOfRules(CSSTokenStream stream, boolean topLevel) throws IOException {
    List<CSSRule> rules = new ArrayList<>();

    // TODO: Other cases
    while (true) {
      Token token = stream.read();
      switch (token) {
        case WhitespaceToken _:
          continue;
        case EOFToken _:
          return rules;
        default:
          stream.unread(token);
          CSSRule rule = consumeAQualifiedRule(stream);
          if (rule != null) {
            rules.add(rule);
          }
          break;
      }
    }
  }

  private CSSRule consumeAQualifiedRule(CSSTokenStream stream) throws IOException {
    List<Token> prelude = new ArrayList<>(4);

    // TODO: Other cases
    while (true) {
      Token token = stream.read();
      switch (token) {
        case LCBracketToken _:
          SimpleBlock simpleBlock = consumeASimpleBlock(stream, token);
          return new QualifiedRule(prelude, simpleBlock);
        default:
          stream.unread(token);
          Token componentValue = consumeAComponentValue(stream);
          prelude.add(componentValue);
          break;
      }
    }
  }

  private Token consumeAComponentValue(CSSTokenStream stream) throws IOException {
    // TODO: Other cases
    Token token = stream.read();
    return token;
  }

  private SimpleBlock consumeASimpleBlock(CSSTokenStream stream, Token associatedToken) throws IOException {
    List<Token> value = new ArrayList<>();

    //TODO: Other cases
    while (true) {
      Token token = stream.read();
      if (associatedToken instanceof LCBracketToken && token instanceof RCBracketToken) {
        return new SimpleBlock(associatedToken, value);
      } else {
          stream.unread(token);
          Token componentValue = consumeAComponentValue(stream);
          value.add(componentValue);
      }
    }
  }
  
}
