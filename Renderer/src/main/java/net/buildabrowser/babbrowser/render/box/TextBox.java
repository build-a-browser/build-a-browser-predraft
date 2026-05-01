package net.buildabrowser.babbrowser.render.box;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.render.box.imp.TextBoxImp;

public interface TextBox extends Box {
  
  Text textNode();

  String text();

  static TextBox create(Text text) {
    return new TextBoxImp(text);
  }

}
