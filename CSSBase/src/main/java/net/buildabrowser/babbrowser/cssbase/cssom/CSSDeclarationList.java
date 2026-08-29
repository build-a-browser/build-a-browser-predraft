package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.List;

public record CSSDeclarationList(
  List<Declaration> declarations
) implements CSSRuleOrDeclarations {

  public static CSSRuleOrDeclarations create(
    List<Declaration> declarations
  ) {
    return new CSSDeclarationList(List.copyOf(declarations));
  }
  
}
