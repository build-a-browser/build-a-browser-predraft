package net.buildabrowser.babbrowser.html.attrparse;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LegacyColorParserTest {

  private static Map<String, Integer> COLOR_MAP = Map.of(
    "transparent", 0xFFFFFF,
    "red", 0xFF0000
  );

  @Test
  @DisplayName("Can parse named color")
  public void canParseNamedColor() {
    int color = LegacyColorParser.parseLegacyColor("red", COLOR_MAP);
    Assertions.assertEquals(0xFF0000, color);
  }
  
  @Test
  @DisplayName("Can parse 3-component color")
  public void canParse3ComponentColor() {
    int color = LegacyColorParser.parseLegacyColor("#AAA", COLOR_MAP);
    Assertions.assertEquals(0xAAAAAA, color);
  }
  
  @Test
  @DisplayName("Can parse 6-component color")
  public void canParse6ComponentColor() {
    int color = LegacyColorParser.parseLegacyColor("#BABBAB", COLOR_MAP);
    Assertions.assertEquals(0xBABBAB, color);
  }
  
  @Test
  @DisplayName("Can parse weird color")
  public void canParseWeirdColor() {
    int color = LegacyColorParser.parseLegacyColor("#r0516n0233g0740", COLOR_MAP);
    Assertions.assertEquals(0x512374, color);
  }

}
