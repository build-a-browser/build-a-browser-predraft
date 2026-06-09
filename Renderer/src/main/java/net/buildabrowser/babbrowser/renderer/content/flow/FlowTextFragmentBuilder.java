package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public class FlowTextFragmentBuilder {
  
  private final StringBuilder textBuilder = new StringBuilder();

  private float width = 0;
  private float height = 0;

  public void addText(String text, float width, float height) {
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

  public TextFragment commit() {
    TextFragment result = new TextFragment(width, height, textBuilder.toString());
    this.width = 0;
    this.height = 0;
    textBuilder.setLength(0);

    return result;
  }

}
