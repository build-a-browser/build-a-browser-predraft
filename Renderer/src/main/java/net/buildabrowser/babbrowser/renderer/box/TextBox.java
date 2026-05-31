package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.imp.TextBoxImp;

public interface TextBox extends Box {
  
  Text textNode();

  String text();

  static TextBox create(Text text) {
    return new TextBoxImp(text);
  }

}
