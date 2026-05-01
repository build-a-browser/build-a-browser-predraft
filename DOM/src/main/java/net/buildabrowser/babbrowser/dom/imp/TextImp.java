package net.buildabrowser.babbrowser.dom.imp;

import net.buildabrowser.babbrowser.dom.Text;

public class TextImp extends NodeImp implements Text {

  private final StringBuilder data;

  public TextImp(String text) {
    this.data = new StringBuilder(text);
  }

  @Override
  public String data() {
    return this.data.toString();
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  @Override
  public void appendCharacter(int ch) {
    data.appendCodePoint(ch);
  }

  @Override
  public void appendString(String data) {
    this.data.append(data);
  }
  
}
