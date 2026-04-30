package net.buildabrowser.babbrowser.css.engine.property.background;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

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
  }

}
