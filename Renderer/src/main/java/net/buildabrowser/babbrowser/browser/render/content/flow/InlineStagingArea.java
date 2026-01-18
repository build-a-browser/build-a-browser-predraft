package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;

public class InlineStagingArea {
  
  private List<StagingElement> stagedElements = new ArrayList<>();
  private int cursor = 0;

  public void resetCursor() {
    this.cursor = 0;
  }
  
  public void pushStagedElement(StagingElement stagingElement) {
    stagedElements.add(stagingElement);
  }

  public StagingElement next() {
    return stagedElements.get(cursor++);
  }

  public boolean done() {
    return this.cursor == stagedElements.size();
  }

  public int cursorPos() {
    return this.cursor;
  }

  public int textBefore(int pos) {
    for (int i = pos - 1; i >= 0; i--) {
      if (stagedElements.get(i) instanceof StagedText) {
        return i;
      }
    }
    return -1;
  }

  public int textAfter(int pos) {
    for (int i = pos + 1; i < stagedElements.size(); i++) {
      if (stagedElements.get(i) instanceof StagedText) {
        return i;
      }
    }
    return -1;
  }

  public String textAt(int i) {
    return ((StagedText) stagedElements.get(i)).currentText();
  }

  public void setText(int i, String text) {
    StagedText oldStagedText = (StagedText) stagedElements.get(i);
    stagedElements.set(i, new StagedText(oldStagedText.boxRef(), text));
  }

  public StagingElement stagingElementAt(int i) {
    return stagedElements.get(i);
  }

  public void removeStagingElementAt(int i) {
    stagedElements.remove(i);
    if (i <= cursor) {
      this.cursor--;
    }
  }

  //

  public static interface StagingElement {}

  public record StagedText(TextBox boxRef, String currentText) implements StagingElement {}

  public record StagedFloatBox(ElementBox elementBox) implements StagingElement {}

  public record StagedUnmanagedBox(ElementBox elementBox) implements StagingElement {}

  public record StagedBlockLevelBox(ElementBox elementBox) implements StagingElement {}

  public record ManagedBoxEntryMarker(ElementBox elementBox) implements StagingElement {}

  public record ManagedBoxExitMarker(ElementBox elementBox) implements StagingElement {}

}
