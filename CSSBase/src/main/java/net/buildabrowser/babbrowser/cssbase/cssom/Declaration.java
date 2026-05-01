package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

// TODO: Case-Sensitive option?
public class Declaration {

  private final String name;
  private final List<Token> value;
  private final boolean important;

  private CSSValue evaluation;

  public Declaration(String name, List<Token> value, boolean important) {
    this.name = name;
    this.value = value;
    this.important = important;
  }

  public String name() {
    return this.name;
  }

  public List<Token> value() {
    return this.value;
  }

  public boolean important() {
    return this.important;
  }

  // TODO: Account for deferred variables
  public CSSValue evaluate() {
    if (evaluation == null) {
      this.evaluation = DeclarationParser.parseDeclaration(this);
    }

    return this.evaluation;
  }

  // For testing purposes:
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Declaration other)) return false;
    return
      name.equals(other.name)
      && important == other.important
      && value.equals(other.value);
  }

  public static Declaration create(String name, List<Token> value, boolean important) {
    if (DeclarationParser.isKnownDeclarationName(name)) {
      name = name.intern();
    }
    return new Declaration(name, value, important);
  }

}
