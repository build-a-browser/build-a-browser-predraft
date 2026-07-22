package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceCollapseValue;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedText;

public class LineWhitespaceCollapserTest {
  
  @Test
  @DisplayName("Can preserve spaces")
  public void canPreserveSpaces() {
    InlineStagingArea staged = stageText("HELLO\n\t WORLD!");
    LineWhiteSpaceCollapser.collapseWhiteSpace(staged, WhiteSpaceCollapseValue.PRESERVE_SPACES);

    List<String> expectedText = List.of("HELLO   WORLD!");
    List<String> actualText = collectText(staged);
    Assertions.assertEquals(expectedText, actualText);
  }

  @Test
  @DisplayName("Can collapse tabs and newlines")
  public void canCollapseTabsAndNewlines() {
    InlineStagingArea staged = stageText("Kitsunes are\tsuper\ncool!");
    LineWhiteSpaceCollapser.collapseWhiteSpace(staged, WhiteSpaceCollapseValue.COLLAPSE);

    List<String> expectedText = List.of("Kitsunes are super cool!");
    List<String> actualText = collectText(staged);
    Assertions.assertEquals(expectedText, actualText);
  }

  @Test
  @DisplayName("Can collapse extra spacing")
  public void canCollapseExtraSpacing() {
    InlineStagingArea staged = stageText(" \n\tGoodbye   Cruel\n\nWorld!\t");
    LineWhiteSpaceCollapser.collapseWhiteSpace(staged, WhiteSpaceCollapseValue.COLLAPSE);

    List<String> expectedText = List.of(" Goodbye \u200B\u200BCruel World! ");
    List<String> actualText = collectText(staged);
    Assertions.assertEquals(expectedText, actualText);
  }

  private InlineStagingArea stageText(String... texts) {
    InlineStagingArea stagingArea = new InlineStagingArea();
    for (String text: texts) {
      stagingArea.pushStagedElement(new StagedText(null, null, text, null));
    }

    return stagingArea;
  }

  private List<String> collectText(InlineStagingArea staged) {
    List<String> allText = new ArrayList<>();
    int nextText = staged.textAfter(-1);
    while (nextText != -1) {
      allText.add(staged.textAt(nextText));
      nextText = staged.textAfter(nextText);
    }

    return allText;
  }

}
