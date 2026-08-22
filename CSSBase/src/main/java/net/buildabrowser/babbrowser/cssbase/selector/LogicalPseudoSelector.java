package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;

public record LogicalPseudoSelector(
  LogicalPseudoSelectorType type,
  List<ComplexSelector> complexSelectors
) implements SelectorPart {

  public static enum LogicalPseudoSelectorType {
    IS, NOT, WHERE, HAS
  }

  @Override
  public String serialize() {
    return new StringBuilder()
      .append(':')
      .append(CSSSerializerUtil.serializeEnum(type))
      .append('(')
      .append(CSSSerializerUtil.serializeSelectorList(complexSelectors))
      .append(')')
      .toString();
    
  }

}
