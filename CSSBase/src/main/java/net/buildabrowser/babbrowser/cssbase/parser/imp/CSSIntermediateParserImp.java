package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.FunctionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
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
        case EOFToken _:
          // TODO: Report parse error
          return null;
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

  public List<Declaration> consumeAStyleBlocksContents(CSSTokenStream stream) throws IOException {
    List<Declaration> declarations = new ArrayList<>(4);

    // TODO: Other cases
    while (true) {
      Token token = stream.read();
      switch (token) {
        case WhitespaceToken _, SemicolonToken _:
          continue;
        case EOFToken _:
          // TODO: Extend decl with rules
          return declarations;
        case IdentToken _:
          handleStyleBlockIdent(stream, declarations, token);
          break;
        default:
          throw new UnsupportedOperationException("Not yet implemented!");
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


    Declaration declaration = consumeADeclaration(ListCSSTokenStream.create(tempTokens));
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
    while (declValue.getLast() instanceof WhitespaceToken) {
      declValue.removeLast();
    }

    return new Declaration(nameToken.value(), declValue, false);
  }

  private Token consumeAComponentValue(CSSTokenStream stream) throws IOException {
    // TODO: Other cases
    Token token = stream.read();
    if (token instanceof FunctionToken functionToken) {
      return consumeAFunction(stream, functionToken);
    }

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
