package net.buildabrowser.babbrowser.cssbase.property.color;

import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;

public final class ColorParserTestUtil {
  
  private ColorParserTestUtil() {}

  public static void initColors() {
    NamedColorParser.setNamedColors(Map.of(
      "red", SRGBAColor.create(255, 0, 0, 255),
      "green", SRGBAColor.create(0, 255, 0, 255),
      "blue", SRGBAColor.create(0, 0, 255, 255),
      "rebeccapurple", SRGBAColor.create(102, 51, 153, 255)
    ));
  }

}
