package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

public class TextFragment extends LayoutFragment {

  private final String text;

  public TextFragment(int width, int height, String text) {
    super(width, height);
    this.text = text;
  }

  public TextFragment(int x, int y, int width, int height, String text) {
    super(width, height);
    setPos(x, y);
    this.text = text;
  }

  public String text() {
    return this.text;
  };

  @Override
  public String toString() {
    return "[TextFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "] text=[" + text() + "]]";
  }

}