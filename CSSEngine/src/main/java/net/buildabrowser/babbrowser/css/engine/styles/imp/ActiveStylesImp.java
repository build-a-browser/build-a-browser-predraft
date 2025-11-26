package net.buildabrowser.babbrowser.css.engine.styles.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class ActiveStylesImp implements ActiveStyles {

  private int textColor = 0xFF000000;

  @Override
  public int textColor() {
    return this.textColor;
  }

  @Override
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }
  
}
