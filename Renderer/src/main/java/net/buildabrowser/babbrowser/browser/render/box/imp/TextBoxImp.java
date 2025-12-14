package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.dom.Text;

public record TextBoxImp(Text textNode) implements TextBox {

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    // TODO: Implement
  }

  @Override
  public String text() {
    return textNode.data();
  }
  
}
