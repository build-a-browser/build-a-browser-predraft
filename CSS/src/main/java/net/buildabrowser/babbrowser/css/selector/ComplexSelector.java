package net.buildabrowser.babbrowser.css.selector;

import java.util.List;

public record ComplexSelector(List<SelectorPart> parts) {

  public static ComplexSelector create(List<SelectorPart> parts) {
    return new ComplexSelector(parts);
  }

}
