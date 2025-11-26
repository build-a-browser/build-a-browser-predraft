package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;

public interface ActiveStyles {
  
  int textColor();

  void setTextColor(int textColor);

  static ActiveStyles create() {
    return new ActiveStylesImp();
  }

}
