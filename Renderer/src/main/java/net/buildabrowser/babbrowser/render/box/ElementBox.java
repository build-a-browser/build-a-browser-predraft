package net.buildabrowser.babbrowser.render.box;

import java.util.Comparator;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.render.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public interface ElementBox extends Box {

  ActiveStyles activeStyles();
  
  BoxContent content();

  HTMLElement element();

  Box parentBox();

  ElementBoxDimensions dimensions();

  ElementBoxIterator childBoxes();

  void addChild(Box box);

  void clearChildren();

  // TODO: I don't really like this method, but it is needed for order-modified fixup
  void sortChildren(Comparator<? super Box> comparator);

  BoxLevel boxLevel();

  UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void updatePositioningFragment(BoxFragment boxFragment);

  BoxFragment positioningFragment();

  LayoutContext layoutContext();

  void setLayoutContext(LayoutContext layoutContext);

  StackingContext stackingContext();

  void setStackingContext(StackingContext stackingContext);

  void update();

  default boolean isReplaced() {
    return content().isReplaced();
  }
 
  public static ElementBox create(HTMLElement element, Box parentBox, BoxLevel boxLevel) {
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
