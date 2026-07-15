package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public class FlowTextFragmentBuilder {
  
  private final StringBuilder textBuilder = new StringBuilder();

  private MappingRLEBuffer originalRleBuffer;
  private MappingRLEBuffer rleBuffer;

  private Node sourceNode = null;
  private int lastIndex = 0;

  private float width = 0;
  private float height = 0;

  public void startText(
    Node sourceNode,
    MappingRLEBuffer reference
  ) {
    assert textBuilder.length() == 0;
    textBuilder.setLength(0);
    this.sourceNode = sourceNode;
    this.originalRleBuffer = reference;
  }

  public void addText(
    String text, int sourceIndex,
    float width, float height
  ) {
    assert sourceIndex >= lastIndex || sourceNode == null;
    if (
      this.originalRleBuffer != null
      && this.rleBuffer == null
      && (sourceIndex > 0 || text.length() > 0)
    ) {
      this.rleBuffer = originalRleBuffer.clone();
    }
    if (rleBuffer != null && sourceIndex > lastIndex) {
      rleBuffer.deleteRange(lastIndex, sourceIndex - 1);
    }
    this.lastIndex = sourceIndex + text.length();

    this.width += width;
    this.height = Math.max(this.height, height);
    textBuilder.append(text);
  }

  public boolean isEmpty() {
    return textBuilder.length() == 0;
  }

  public float height() {
    return this.height;
  }

  public TextFragment commit(FontMetrics fontMetrics) {
    boolean skipRuns =
      rleBuffer == null
      || sourceNode == null
      || (
        sourceNode instanceof Text text
        && rleBuffer.matchesSource(text.data().length()));
    short[] sourceRuns = skipRuns ?
      null : rleBuffer.toShortArray();
    // TODO: Also need to delete tail

    float firstBaseline = 0; // TODO: Compute first baseline
    float lastBaseline = fontMetrics.descent(); // TODO: Respect fallback fonts

    TextFragment result = new TextFragment(
      width, height,
      firstBaseline, lastBaseline,
      sourceNode, textBuilder.toString(), sourceRuns);
    this.lastIndex = 0;
    this.rleBuffer = null;
    this.width = 0;
    this.height = 0;
    textBuilder.setLength(0);

    return result;
  }

}
