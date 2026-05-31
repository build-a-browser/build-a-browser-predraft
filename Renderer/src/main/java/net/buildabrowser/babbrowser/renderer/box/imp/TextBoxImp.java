package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.TextBox;

public class TextBoxImp extends AbstractBoxImp implements TextBox {
  
  private Text textNode;

  public TextBoxImp(Text textNode) {
    this.textNode = textNode;
  }

  @Override
  public Text textNode() {
    return this.textNode;
  }

  @Override
  public String text() {
    return textNode.data();
  }
  
}
