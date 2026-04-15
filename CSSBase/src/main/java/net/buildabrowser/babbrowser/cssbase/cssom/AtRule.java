package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public record AtRule(
  Token name, List<Token> prelude, SimpleBlock simpleBlock
) implements CSSRule, Token {
  
}
