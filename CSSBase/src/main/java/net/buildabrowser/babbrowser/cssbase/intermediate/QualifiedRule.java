package net.buildabrowser.babbrowser.cssbase.intermediate;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public record QualifiedRule(
  List<Token> prelude,
  List<Declaration> declarations,
  List<CSSRule> rules
) implements CSSRule, Token {
  
}
