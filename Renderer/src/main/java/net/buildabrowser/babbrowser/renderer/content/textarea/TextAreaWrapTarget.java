package net.buildabrowser.babbrowser.renderer.content.textarea;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.buildabrowser.babbrowser.renderer.content.common.TextWrapper.TextWrapTarget;

public class TextAreaWrapTarget implements TextWrapTarget {

  private final List<String> lines = new ArrayList<>();
  private final BitSet continuations = new BitSet(16);
  private final StringBuilder lineBuilder = new StringBuilder("");

  private final float maxWidth;
  
  private boolean isSoftWrap = false;
  private float currentWidth = 0;
  private float largestWidth = 0;

  public TextAreaWrapTarget(float maxWidth) {
    this.maxWidth = maxWidth;
  }
  
  @Override
  public void nextLine(boolean isSoftWrap) {
    lines.add(lineBuilder.toString());
    lineBuilder.setLength(0);
    if (this.isSoftWrap) {
      continuations.set(lines.size() - 1);
    }

    this.largestWidth = Math.max(largestWidth, currentWidth);
    this.isSoftWrap = isSoftWrap;
    this.currentWidth = 0;
  }

  @Override
  public boolean fits(float itemSize, boolean forceFirst) {
    if (forceFirst && lineBuilder.length() == 0) {
      return true;
    }

    return currentWidth + itemSize <= maxWidth;
  }

  @Override
  public void appendText(
    String text, int sourceIndex, float width, float height
  ) {
    lineBuilder.append(text);
    this.currentWidth += width;
  }

  @Override
  public boolean ignoreWhitespace() {
    return false;
  }

  public void finish() {
    nextLine(true);
  }

  public List<String> lines() {
    return this.lines;
  }

  public BitSet continuations() {
    return this.continuations;
  }

  public float maxWidth() {
    return this.largestWidth;
  }
  
}
