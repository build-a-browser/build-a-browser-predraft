package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

public record UniversalSelector() implements SimpleSelector {
  
  private static UniversalSelector INSTANCE = new UniversalSelector();

  public static final ComplexSelector AS_COMPLEX_SELECTOR =
    ComplexSelector.create(List.of(INSTANCE));

  public static UniversalSelector create() {
    return INSTANCE;
  }

}
