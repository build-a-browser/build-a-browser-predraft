package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;

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

    return MappingRLEBuffer.sourceIndex(
      textIndex, sourceRuns, sourceRuns.length);
  }

  public int textIndex(int sourceIndex) {
    if (
      !(sourceNode instanceof Text)
      || sourceRuns == null
    ) return sourceIndex;

    return MappingRLEBuffer.textIndex(
      sourceIndex, sourceRuns, sourceRuns.length);
  }

  @Override
  public String toString() {
    return "[TextFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] text=[" + text() + "]]";
  }

}