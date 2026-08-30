package net.buildabrowser.babbrowser.cssbase.parser.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getFirst;
import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;
import static net.buildabrowser.babbrowser.common.util.CompatUtil.removeLast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSDeclarationList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleOrDeclarations;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.AtRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.CSSRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.intermediate.QualifiedRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.AtKeywordToken;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
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

// Includes some changes from https://drafts.csswg.org/css-syntax/
// not present in https://www.w3.org/TR/css-syntax-3/

public class CSSIntermediateParserImp {
  
  public List<CSSRule> consumeAStylesheetsContents(
    CSSTokenStream stream, boolean topLevel
  ) throws IOException {
    List<CSSRule> rules = new ArrayList<>();

    while (true) {
      Token token = stream.peek();
      switch (token) {
        case WhitespaceToken _1:
          stream.read();
          continue;
        case EOFToken _1:
          return rules;
        // TODO: Handle CDO/CDC tokens
        case AtKeywordToken _1:
          CSSRule atRule = consumeAnAtRule(stream, false);
          if (atRule != null) {
            rules.add(atRule);
          }
          break;
        default:
          CSSRule qualifiedRule = consumeAQualifiedRule(
            stream, false, null);
          if (qualifiedRule != null) {
            rules.add(qualifiedRule);
          }
          break;
      }
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static List<CSSRule> wrapDeclarations(List<CSSRuleOrDeclarations> childRules) {
    if (childRules == null) return List.of();

    ListIterator<CSSRuleOrDeclarations> childIt = childRules.listIterator();
    while (childIt.hasNext()) {
      if (childIt.next() instanceof CSSDeclarationList decls) {
        childIt.set(new QualifiedRule(
          null, decls.declarations(), List.of()));
      }
    }

    return (List<CSSRule>) (List) childRules;
  }

  private CSSRule consumeAnAtRule(
    CSSTokenStream stream, boolean nested
  ) throws IOException {
    Token name = stream.read();
    List<Token> prelude = new ArrayList<>(4);

    while (true) {
      Token token = stream.peek();
      switch (token) {
        case SemicolonToken _1:
          stream.read();
          // TODO: Check valid
          return new AtRule(name, prelude, null);
        case EOFToken _1:
          stream.read();
          // TODO: Check valid
          return new AtRule(name, prelude, null);
        case RCBracketToken _1:
          if (nested) {
              // TODO: Check valid
            return new AtRule(name, prelude, null);
          } else {
            prelude.add(stream.read());
            break;
          }
        case LCBracketToken _1: {
          List<CSSRuleOrDeclarations> rules = consumeABlock(stream);
          // TODO: Check valid
          return new AtRule(name, prelude, rules);
        }
        default:
          Token componentValue = consumeAComponentValue(stream);
          prelude.add(componentValue);
          break;
      }
    }
  }

  private CSSRule consumeAQualifiedRule(
    CSSTokenStream stream, boolean nested, Token stopToken
  ) throws IOException {
    List<Token> prelude = new ArrayList<>(4);
    List<Declaration> declarations = List.of();
    List<CSSRule> childRules = List.of();

    // TODO: Other cases
    while (true) {
      Token token = stream.peek();
      if (
        stopToken != null
        && token.equals(stopToken)
      ) {
        // TODO: Report parse error
        return null;
      }

      switch (token) {
        case EOFToken _1:
          // TODO: Report parse error
          return null;
        case RCBracketToken _1:
          // TODO: Report parse error
          if (nested) return null;
          prelude.add(stream.read());
          break;
        case LCBracketToken _1: {
          // TODO: Handle --var: {} case
          List<CSSRuleOrDeclarations> childRules2 = consumeABlock(stream);
          if (getFirst(childRules2) instanceof CSSDeclarationList list) {
            childRules2.remove(0);
            declarations = list.declarations();
          }

          // TODO: Check if valid in current context
          childRules = wrapDeclarations(childRules2);
          return new QualifiedRule(prelude, declarations, childRules);
        }
        default:
          Token componentValue = consumeAComponentValue(stream);
          prelude.add(componentValue);
          break;
      }
    }
  }

  public List<CSSRuleOrDeclarations> consumeABlock(
    CSSTokenStream stream
  ) throws IOException {
    assert stream.peek() instanceof LCBracketToken;
    stream.read();
    List<CSSRuleOrDeclarations> rules = consumeABlocksContents(stream);
    stream.read();
    return rules;
  }

  public List<CSSRuleOrDeclarations> consumeABlocksContents(
    CSSTokenStream stream
  ) throws IOException {
    List<CSSRuleOrDeclarations> rules = new ArrayList<>(4);
    List<Declaration> declarations = new ArrayList<>(4);

    // TODO: Other cases
    while (true) {
      // Peek acts weirdly around mark, so have to do mark first
      // TODO: Make it less weird
      int mark = stream.mark();
      Token token = stream.peek();
      switch (token) {
        case WhitespaceToken _1:
          stream.discardMark();
          stream.read();
          continue;
        case SemicolonToken _1:
          stream.discardMark();
          stream.read();
          continue;
        case EOFToken _1:
          // NOSPEC: https://github.com/w3c/csswg-drafts/issues/11017
          if (!declarations.isEmpty()) {
            rules.add(CSSDeclarationList.create(declarations));
          }
          stream.discardMark();
          return rules;
        case RCBracketToken _1:
          if (!declarations.isEmpty()) {
            rules.add(CSSDeclarationList.create(declarations));
          }
          stream.discardMark();
          return rules;
        case AtKeywordToken _1: {
          stream.discardMark();
          if (!declarations.isEmpty()) {
            rules.add(CSSDeclarationList.create(declarations));
            declarations.clear();
          }

          CSSRule rule = consumeAnAtRule(stream, true);
          if (rule != null) {
            rules.add(rule);
          }
          break;
        }
        default:
          Declaration declaration = consumeADeclaration(stream, true);
          if (declaration != null) {
            declarations.add(declaration);
            stream.discardMark();
          } else {
            stream.restoreMark(mark);
            CSSRule rule = consumeAQualifiedRule(
              stream, true, SemicolonToken.create());
            if (rule != null) {
              if (!declarations.isEmpty()) {
                rules.add(CSSDeclarationList.create(declarations));
                declarations.clear();
              }
              rules.add(rule);
              // TODO: Check invalid
            }
          }
      }
    }
  }

  private Declaration consumeADeclaration(
    CSSTokenStream stream, boolean nested
  ) throws IOException {
    String declName;
    if (stream.peek() instanceof IdentToken identToken) {
      stream.read();
      declName = identToken.value();
    } else {
      consumeRemnantsOfBadDeclaration(stream, nested);
      return null;
    }

    while (stream.peek() instanceof WhitespaceToken) {
      stream.read();
    }

    if (stream.peek() instanceof ColonToken) {
      stream.read();
    } else {
      consumeRemnantsOfBadDeclaration(stream, nested);
      return null;
    }

    while (stream.peek() instanceof WhitespaceToken) {
      stream.read();
    }

    List<Token> declValue = consumeAListOfComponentValues(
      stream, nested, SemicolonToken.create());
    boolean declImportant = removeImportant(declValue);

    while (getLast(declValue) instanceof WhitespaceToken) {
      removeLast(declValue);
    }

    // TODO: Special handling for some other declarations
    if (declName.startsWith("--")) {
      // TODO: Handle thise
    } else if (containsNonEmptySimpleBlock(declValue)) {
      return null;
    }
    // TODO: Check valid

    return Declaration.create(
      stream.source(), declName, declValue, declImportant);
  }

  private boolean containsNonEmptySimpleBlock(List<Token> declValue) {
    for (Token token: declValue) {
      if (token instanceof SimpleBlock simpleBlock) {
        for (Token token2: simpleBlock.value()) {
          if (!(token2 instanceof WhitespaceToken)) return true;
        }
      }
    }

    return false;
  }

  private void consumeRemnantsOfBadDeclaration(
    CSSTokenStream stream, boolean nested
  ) throws IOException {
    while (true) {
      switch (stream.peek()) {
        case EOFToken _1:
          stream.read();
          return;
        case SemicolonToken _1:
          stream.read();
          return;
        case RCBracketToken _1:
          if (nested) return;
          stream.read();
          break;
        default:
          consumeAComponentValue(stream);
      }
    }
  }

  private List<Token> consumeAListOfComponentValues(
    CSSTokenStream stream, boolean nested, Token stopToken
  ) throws IOException {
    List<Token> values = new ArrayList<>();
    while (true) {
      if (stream.peek().equals(stopToken)) {
        return values;
      }

      switch (stream.peek()) {
        case EOFToken _1:
          return values;
        case RCBracketToken _1:
          if (nested) return values;
          // TODO: Parse error
          values.add(stream.read());
          break;
        default:
          values.add(consumeAComponentValue(stream));
      }
    }
  }

  private boolean removeImportant(List<Token> declValue) {
    boolean important = false;
    int lastNonWhitespace1 = lastNonWhitespace(declValue, declValue.size() - 1);
    int lastNonWhitespace2 = lastNonWhitespace(declValue, lastNonWhitespace1 - 1);
    if (
      lastNonWhitespace2 != -1
      && declValue.get(lastNonWhitespace2) instanceof DelimToken delimToken
      && delimToken.ch() == '!'
      && declValue.get(lastNonWhitespace1) instanceof IdentToken identToken
      && identToken.value().equals("important")
    ) {
      important = true;
      declValue.remove(lastNonWhitespace1);
      declValue.remove(lastNonWhitespace2);
    }
    return important;
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

  private int lastNonWhitespace(List<Token> declValue, int i) {
    for (int j = i; j >= 0; j--) {
      if (!(
        declValue.get(j) instanceof WhitespaceToken
      )) return j;
    }

    return -1;
  }
  
}
