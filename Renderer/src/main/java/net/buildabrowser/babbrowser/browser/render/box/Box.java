package net.buildabrowser.babbrowser.browser.render.box;

import javax.swing.JComponent;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public interface Box {
  
  JComponent render(ActiveStyles parentStyles);

}
