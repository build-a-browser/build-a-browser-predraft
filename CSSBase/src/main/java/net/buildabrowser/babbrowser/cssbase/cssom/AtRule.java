package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public record AtRule(
  Token name,
  List<Token> prelude,
  List<CSSRuleOrDeclarations> rules
) implements CSSRule, Token {
  
}
