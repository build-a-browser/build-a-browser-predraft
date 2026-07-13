package net.buildabrowser.babbrowser.renderer.fragment;

public class TextFragment extends LayoutFragment {

  private final String text;
  private final float firstBaseline;
  private final float lastBaseline;

  public TextFragment(
    float width, float height,
    String text,
    float firstBaseline, float lastBaseline
  ) {
    super(width, height);
    this.firstBaseline = firstBaseline;
    this.lastBaseline = lastBaseline;
    this.text = text;
  }

  // For testing
  public TextFragment(
    float x, float y, float width, float height, String text
  ) {
    this(width, height, text, 0, 0);
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

  @Override
  public String toString() {
    return "[TextFragment pos=[" + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] text=[" + text() + "]]";
  }

}