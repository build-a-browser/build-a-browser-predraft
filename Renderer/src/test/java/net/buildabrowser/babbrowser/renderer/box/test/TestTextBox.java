package net.buildabrowser.babbrowser.renderer.box.test;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.TextBox;
import net.buildabrowser.babbrowser.renderer.box.imp.AbstractBoxImp;

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
    return null;
  }
  
}
