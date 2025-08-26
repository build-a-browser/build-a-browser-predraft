package net.buildabrowser.babbrowser.css.intermediate;

import java.util.List;

import net.buildabrowser.babbrowser.css.tokens.Token;

public record SimpleBlock(Token type, List<Token> value) implements Token {

}
