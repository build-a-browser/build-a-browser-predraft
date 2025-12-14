package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.box.imp.TextBoxImp;
import net.buildabrowser.babbrowser.dom.Text;

public interface TextBox extends Box {
  
  Text textNode();

  String text();

  static TextBox create(Text text) {
    return new TextBoxImp(text);
  }

}
