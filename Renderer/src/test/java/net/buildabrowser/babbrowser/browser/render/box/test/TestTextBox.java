package net.buildabrowser.babbrowser.browser.render.box.test;

import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.dom.Text;

public class TestTextBox implements TextBox {

  private final String text;

  public TestTextBox(String text) {
    this.text = text;
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    
  }

  @Override
  public String text() {
    return this.text;
  }

  @Override
  public Text textNode() {
    throw new UnsupportedOperationException();
  }
  
}
