package net.buildabrowser.babbrowser.css.intermediate;

import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSRule;
import net.buildabrowser.babbrowser.css.tokens.Token;

public record QualifiedRule(List<Token> prelude, SimpleBlock simpleBlock) implements CSSRule, Token {
  
}
