package net.buildabrowser.babbrowser.cssbase.parser.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;
import static net.buildabrowser.babbrowser.common.util.CompatUtil.removeLast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.AtRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.AtKeywordToken;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.FunctionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class CSSIntermediateParserImp {
  
  public List<CSSRule> consumeAListOfRules(CSSTokenStream stream, boolean topLevel) throws IOException {
    List<CSSRule> rules = new ArrayList<>();

    // TODO: Other cases
    while (true) {
      Token token = stream.read();
      switch (token) {
        case WhitespaceToken _1:
          continue;
        case EOFToken _1:
          return rules;
        case AtKeywordToken _1:
          stream.unread(token);
          CSSRule atRule = consumeAnAtRule(stream);
          rules.add(atRule);
          break;
        default:
          stream.unread(token);
          CSSRule qualifiedRule = consumeAQualifiedRule(stream);
          if (qualifiedRule != null) {
            rules.add(qualifiedRule);
          }
          break;
      }
    }
  }

  private CSSRule consumeAnAtRule(CSSTokenStream stream) throws IOException {
    Token name = stream.read();
    List<Token> prelude = new ArrayList<>(4);

    while (true) {
      Token token = stream.read();
      switch (token) {
        case SemicolonToken _1:
          return new AtRule(name, prelude, null);
        case EOFToken _1:
          // TODO: Report parse error
          return new AtRule(name, prelude, null);
        case LCBracketToken _1: {
          SimpleBlock simpleBlock = consumeASimpleBlock(stream, token);
          return new AtRule(name, prelude, simpleBlock);
        }
        case SimpleBlock simpleBlock:
          if (simpleBlock.type() instanceof LCBracketToken) {
            return new AtRule(name, prelude, simpleBlock);
          }
          // Fall-through
        default:
          stream.unread(token);
          Token componentValue = consumeAComponentValue(stream);
          prelude.add(componentValue);
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
        case EOFToken _1:
          // TODO: Report parse error
          return null;
        case LCBracketToken _1: {
          SimpleBlock simpleBlock = consumeASimpleBlock(stream, token);
          return new QualifiedRule(prelude, simpleBlock);
        }
        case SimpleBlock simpleBlock:
          if (simpleBlock.type() instanceof LCBracketToken) {
            return new QualifiedRule(prelude, simpleBlock);
          }
          // Fall-through
        default:
          stream.unread(token);
          Token componentValue = consumeAComponentValue(stream);
          prelude.add(componentValue);
          break;
      }
    }
  }

  public List<Declaration> consumeAStyleBlocksContents(CSSTokenStream stream) throws IOException {
    List<Declaration> declarations = new ArrayList<>(4);

    // TODO: Other cases
    while (true) {
      Token token = stream.read();
      switch (token) {
        case WhitespaceToken _1:
          continue;
        case SemicolonToken _1:
          continue;
        case EOFToken _1:
          // TODO: Extend decl with rules
          return declarations;
        case IdentToken _1:
          handleStyleBlockIdent(stream, declarations, token);
          break;
        default:
          //throw new UnsupportedOperationException("Not yet implemented!");
      }
    }
  }

  private void handleStyleBlockIdent(CSSTokenStream stream, List<Declaration> declarations, Token firstToken) throws IOException {
    List<Token> tempTokens = new ArrayList<>(3);
    tempTokens.add(firstToken);
    while (!(((firstToken = stream.read()) instanceof EOFToken) || firstToken instanceof SemicolonToken)) {
      stream.unread(firstToken);
      tempTokens.add(consumeAComponentValue(stream));
    }


    Declaration declaration = consumeADeclaration(
      ListCSSTokenStream.create(stream.source(), tempTokens));
    if (declaration != null) {
      declarations.add(declaration);
    }
  }

  private Declaration consumeADeclaration(CSSTokenStream stream) throws IOException {
    List<Token> declValue = new ArrayList<>(1);
    IdentToken nameToken = (IdentToken) stream.read();
    Token token;
    while ((token = stream.read()) instanceof WhitespaceToken);
    if (!(token instanceof ColonToken)) return null; // Parse Error
    while ((token = stream.read()) instanceof WhitespaceToken);
    stream.unread(token);
    while (!((token = stream.read()) instanceof EOFToken)) {
      stream.unread(token);
      declValue.add(consumeAComponentValue(stream));
    }

    // TODO: !important
    if (declValue.isEmpty()) return null;
    while (getLast(declValue) instanceof WhitespaceToken) {
      removeLast(declValue);
    }

    return Declaration.create(
      stream.source(), nameToken.value(), declValue, false);
  }

  private Token consumeAComponentValue(CSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (
      token instanceof LCBracketToken
      || token instanceof LSBracketToken
      || token instanceof LParenToken
    ) {
      return consumeASimpleBlock(stream, token);
    } else if (token instanceof FunctionToken functionToken) {
      return consumeAFunction(stream, functionToken);
    } else {
      return token;
    }
  }

  private SimpleBlock consumeASimpleBlock(CSSTokenStream stream, Token associatedToken) throws IOException {
    List<Token> value = new ArrayList<>();

    while (true) {
      Token token = stream.read();
      if (
        (associatedToken instanceof LCBracketToken && token instanceof RCBracketToken)
        || (associatedToken instanceof LSBracketToken && token instanceof RSBracketToken)
        || (associatedToken instanceof LParenToken && token instanceof RParenToken)
      ) {
        return new SimpleBlock(associatedToken, value);
      } else if (token instanceof EOFToken) {
        return new SimpleBlock(associatedToken, value);
      } else {
        stream.unread(token);
        Token componentValue = consumeAComponentValue(stream);
        value.add(componentValue);
      }
    }
  }

  private Token consumeAFunction(CSSTokenStream stream, FunctionToken functionToken) throws IOException {
    FunctionValue function = new FunctionValue(functionToken.value(), new ArrayList<>());
    while (true) {
      Token token = stream.read();
      if (token instanceof RParenToken) {
        return function;
      } else if (token instanceof EOFToken) {
        // TODO: Report parse error
        return function;
      } else {
        stream.unread(token);
        function.value().add(consumeAComponentValue(stream));
      }
    }
  }
  
}
