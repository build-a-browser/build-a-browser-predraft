package net.buildabrowser.babbrowser.browser.render.box;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.browser.render.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public interface ElementBox extends Box {

  ActiveStyles activeStyles();
  
  BoxContent content();

  Element element();

  ElementBoxDimensions dimensions();

  List<Box> childBoxes();

  void addChild(Box box);

  void removeChild(Box box);

  void removeChild(int i);

  void clearChildren();

  BoxLevel boxLevel();

  default boolean isReplaced() {
    return content().isReplaced();
  };
 
  public static ElementBox create(MutableElement element, Box parentBox, BoxLevel boxLevel) {
    return new ElementBoxImp(element, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(ElementBox parentBox, BoxLevel boxLevel) {
    ActiveStyles styles = ActiveStyles.create(parentBox.activeStyles());
    return new AnonymousElementBoxImp(styles, parentBox, boxLevel);
  }

  public static ElementBox createAnonymous(ActiveStyles styles, ElementBox parentBox, BoxLevel boxLevel) {
    return new AnonymousElementBoxImp(styles, parentBox, boxLevel);
  }

  enum BoxLevel {
    BLOCK_LEVEL, INLINE_LEVEL
  }

}
