package net.buildabrowser.babbrowser.cssbase.property.background;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BackgroundValue(
  List<BackgroundLayer> bgLayers
) implements CSSValue {

  public static BackgroundValue create(List<BackgroundLayer> bgLayers) {
    return new BackgroundValue(bgLayers);
  }
  
  public static record BackgroundLayer(
    CSSValue bgImage,
    CSSValue bgPosition,
    CSSValue bgSize,
    CSSValue repeatStyle,
    CSSValue attachment,
    CSSValue bgOrigin,
    CSSValue bgClip,
    CSSValue bgColor
  ) implements CSSValue {

    public static BackgroundLayer create(
      CSSValue bgImage,
      CSSValue bgPosition,
      CSSValue bgSize,
      CSSValue repeatStyle,
      CSSValue attachment,
      CSSValue bgOrigin,
      CSSValue bgClip,
      CSSValue bgColor
    ) {
      return new BackgroundLayer(
        bgImage, bgPosition, bgSize, repeatStyle,
        attachment, bgOrigin, bgClip, bgColor);
    }

    @Override
    public String serialize() {
      String serialized = CSSSerializerUtil.serializeManySpaces(
        bgImage, repeatStyle, attachment, bgOrigin, bgClip, bgColor, bgPosition);
      return bgSize == null ?
        serialized :
        serialized + " / " + bgSize;
    }

  }

  @Override
  public String serialize() {
    return CSSSerializerUtil.serializeManyCommas(
      bgLayers.toArray(new BackgroundLayer[0]));
  }

}
