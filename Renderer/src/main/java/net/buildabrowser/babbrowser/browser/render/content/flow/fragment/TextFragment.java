package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

public class TextFragment extends FlowFragment {

  private final String text;

  public TextFragment(int width, int height, String text) {
    super(width, height);
    this.text = text;
  }

  public String text() {
    return this.text;
  };

}