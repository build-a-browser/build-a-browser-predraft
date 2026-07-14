package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public class FlowTextFragmentBuilder {
  
  private final StringBuilder textBuilder = new StringBuilder();
  // TODO: Use an alternate collection to avoid autoboxing
  // (but not ShortBuffer because that does not auto-resize)
  private final List<Short> rleList = new ArrayList<>(2);

  private Node lastNode = null;
  private int lastIndex = 0;

  private float width = 0;
  private float height = 0;

  public void addText(
    Node sourceNode, String text, int sourceIndex,
    float width, float height
  ) {
    assert sourceIndex >= lastIndex;
    assert lastNode == null || lastNode == sourceNode;
    if (sourceIndex > lastIndex || sourceIndex == 0) {
      pushRLE(sourceIndex - lastIndex);
      pushRLE(text.length());
    } else {
      int lastIndex = rleList.size() - 1;
      int oldLength = Short.toUnsignedInt(rleList.remove(lastIndex));
      pushRLE(oldLength + text.length());
    }
    this.lastIndex = sourceIndex + text.length();
    this.lastNode = sourceNode;

    this.width += width;
    this.height = Math.max(this.height, height);
    textBuilder.append(text);
  }

  private void pushRLE(int value) {
    int maxValue = Short.toUnsignedInt((short) -1);
    while (value >= maxValue) {
      value -= maxValue;
      rleList.add((short) -1);
    }
    rleList.add((short) value);
  }

  public boolean isEmpty() {
    return textBuilder.length() == 0;
  }

  public Node lastNode() {
    return this.lastNode;
  }

  public float height() {
    return this.height;
  }

  public TextFragment commit(FontMetrics fontMetrics) {
    boolean skipRuns =
      rleList.size() == 2
      && rleList.get(0) == 0
      && lastNode instanceof Text text
      && Short.toUnsignedInt(rleList.get(1)) == text.data().length();
    short[] sourceRuns = skipRuns ? null : new short[rleList.size()];
    if (!skipRuns) {
      int i = 0;
      for (short run: rleList) {
        sourceRuns[i++] = run;
      }
    }

    float firstBaseline = 0; // TODO: Compute first baseline
    float lastBaseline = fontMetrics.descent(); // TODO: Respect fallback fonts

    TextFragment result = new TextFragment(
      width, height,
      firstBaseline, lastBaseline,
      lastNode, textBuilder.toString(), sourceRuns);
    this.lastNode = null;
    this.lastIndex = 0;
    this.width = 0;
    this.height = 0;
    textBuilder.setLength(0);
    rleList.clear();

    return result;
  }

}
