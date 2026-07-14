package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;

public class TextFragment extends LayoutFragment {

  private final Node sourceNode;
  private final String text;
  private final short[] sourceRuns;

  private final float firstBaseline;
  private final float lastBaseline;

  public TextFragment(
    float width, float height,
    float firstBaseline, float lastBaseline,
    Node sourceNode, String text, short[] sourceRuns
  ) {
    super(width, height);
    this.firstBaseline = firstBaseline;
    this.lastBaseline = lastBaseline;
    this.sourceNode = sourceNode;
    this.text = text;
    this.sourceRuns = sourceRuns;
  }

  // For testing
  public TextFragment(
    float x, float y, float width, float height,
    String text
  ) {
    this(
      width, height,
      0, 0,
      null, text, null);
    setPos(x, y);
  }

  @Override
  public float firstBaseline(Measurement measurement) {
    return this.firstBaseline;
  }

  @Override
  public float lastBaseline(Measurement measurement) {
    return this.lastBaseline;
  }

  public String text() {
    return this.text;
  }

  public Node sourceNode() {
    return this.sourceNode;
  }

  public int sourceIndex(int textIndex) {
    if (
      !(sourceNode instanceof Text)
      || sourceRuns == null
    ) return textIndex;

    int sourceIndex = 0;
    int currentTextIndex = 0;
    boolean isCollapseMode = true;
    int rlePointer = 0;
    while (
      currentTextIndex < textIndex
      || (rlePointer < sourceRuns.length && isCollapseMode)
    ) {
      short rleRaw = sourceRuns[rlePointer];
      int rleLen = Short.toUnsignedInt(rleRaw);
      boolean isModeChange = rleRaw != -1;
      rlePointer++;

      if (isCollapseMode) {
        sourceIndex += rleLen;
      } else {
        if (currentTextIndex + rleLen >= textIndex) {
          return sourceIndex + (textIndex - currentTextIndex);
        }

        sourceIndex += rleLen;
        currentTextIndex += rleLen;
      }

      if (isModeChange) {
        isCollapseMode = !isCollapseMode;
      }
    }

    throw new IllegalStateException("No mapping for textIndex!");
  }

  public int textIndex(int sourceIndex) {
    if (
      !(sourceNode instanceof Text)
      || sourceRuns == null
    ) return sourceIndex;

    int textIndex = 0;
    int currentSourceIndex = 0;
    boolean isCollapseMode = true;
    int rlePointer = 0;
    while (currentSourceIndex < sourceIndex) {
      if (rlePointer >= sourceRuns.length) return text.length();
      short rleRaw = sourceRuns[rlePointer];
      int rleLen = Short.toUnsignedInt(rleRaw);
      boolean isModeChange = rleLen != -1;
      rlePointer++;

      if (isCollapseMode) {
        currentSourceIndex += rleLen;
      } else {
        if (currentSourceIndex + rleLen >= sourceIndex) {
          return textIndex + (sourceIndex - currentSourceIndex);
        }

        currentSourceIndex += rleLen;
        textIndex += rleLen;
      }

      if (isModeChange) {
        isCollapseMode = !isCollapseMode;
      }
    }

    return textIndex;
  }

  @Override
  public String toString() {
    return "[TextFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] text=[" + text() + "]]";
  }

}