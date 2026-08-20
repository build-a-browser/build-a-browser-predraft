package net.buildabrowser.babbrowser.cssbase.property.flex;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FlexFlowValue(FlexDirectionValue direction, FlexWrapValue wrap) implements CSSValue {

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManySpaces(direction, wrap);
  }
  
  public static FlexFlowValue create(FlexDirectionValue direction, FlexWrapValue wrap) {
    return new FlexFlowValue(direction, wrap);
  }

}
