package net.buildabrowser.babbrowser.css.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.css.tokens.Token;

// TODO: Case-Sensitive option?
public record Declaration(String name, List<Token> value, boolean important) {
  
  public static Declaration create(String name, List<Token> value, boolean important) {
    return new Declaration(name, value, important);
  }

}
