package net.buildabrowser.babbrowser.cssbase.selector;

import net.buildabrowser.babbrowser.cssbase.microsyntax.ANPlusB;
import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;

public record NthChildPseudoSelector(
  NthChildPseudoSelectorType type,
  ANPlusB index,
  ComplexSelector selector
) implements SelectorPart {

  @Override
  public String serialize() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(':');
    stringBuilder.append(CSSSerializerUtil.serializeEnum(type));
    if (type.equals(NthChildPseudoSelectorType.ONLY_CHILD)) {
      return stringBuilder.toString();
    }

    stringBuilder
      .append('(')
      .append(index.serialize());
    if (selector != null) {
      stringBuilder.append(" of ");
      stringBuilder.append(selector.serialize());
    }
    stringBuilder.append(')');

    return stringBuilder.toString();
  }
  
  public static enum NthChildPseudoSelectorType {
    NTH, NTH_LAST, ONLY_CHILD
  }

}
