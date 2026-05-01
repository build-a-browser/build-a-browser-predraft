package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.html.imp.HTMLTextImp;

public interface HTMLText extends Text {
  
  // Extensions

  Object getBox();

  void setBox(Object box);

  static HTMLText create(String text) {
    return new HTMLTextImp(text);
  }

}
