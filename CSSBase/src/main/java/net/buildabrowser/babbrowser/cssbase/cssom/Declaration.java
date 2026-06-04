package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

// TODO: Case-Sensitive option?
public class Declaration {

  private final CSSTokenStreamSource source;
  private final String name;
  private final List<Token> value;
  private final boolean important;

  private CSSValue evaluation;

  public Declaration(
    CSSTokenStreamSource source,
    String name, List<Token> value, boolean important
  ) {
    this.source = source;
    this.name = name;
    this.value = value;
    this.important = important;
  }

  public Declaration(
    CSSTokenStreamSource source,
    String name, CSSValue evaluation, boolean important
  ) {
    this.source = source;
    this.name = name;
    this.value = List.of();
    this.important = important;
    this.evaluation = evaluation;
  }

  public CSSTokenStreamSource source() {
    return this.source;
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

  public CSSValue evaluate() {
    if (evaluation == null) {
      this.evaluation = CommonUtil.rethrow(
        () -> DeclarationParser.parseDeclaration(source, this));
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

  public static Declaration create(
    CSSTokenStreamSource source,
    String name, List<Token> value, boolean important
  ) {
    if (DeclarationParser.isKnownDeclarationName(name)) {
      name = name.intern();
    }
    return new Declaration(source, name, value, important);
  }

  public static Declaration create(
    CSSTokenStreamSource source,
    String name, CSSValue value, boolean important
  ) {
    if (DeclarationParser.isKnownDeclarationName(name)) {
      name = name.intern();
    }
    return new Declaration(source, name, value, important);
  }

}
