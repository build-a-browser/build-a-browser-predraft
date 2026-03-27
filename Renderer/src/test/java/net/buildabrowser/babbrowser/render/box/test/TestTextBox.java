package net.buildabrowser.babbrowser.render.box.test;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.render.box.TextBox;
import net.buildabrowser.babbrowser.render.box.imp.AbstractBoxImp;

public class TestTextBox extends AbstractBoxImp implements TextBox {

  private final String text;

  public TestTextBox(String text) {
    this.text = text;
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
