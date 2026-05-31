package net.buildabrowser.babbrowser.renderer.content.common.fragment;

public class TextFragment extends LayoutFragment {

  private final String text;

  public TextFragment(float width, float height, String text) {
    super(width, height);
    this.text = text;
  }

  public TextFragment(float x, float y, float width, float height, String text) {
    super(width, height);
    setPos(x, y);
    this.text = text;
  }

  public String text() {
    return this.text;
  }

  @Override
  public String toString() {
    return "[TextFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] text=[" + text() + "]]";
  }

}