package net.buildabrowser.babbrowser.renderer.box;

import java.util.Comparator;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.imp.AnonymousElementBoxImp;
import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

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

  void updateDetails(Box parentBox, BoxLevel boxLevel);

  UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void updatePositioningFragment(BoxFragment boxFragment);

  BoxFragment positioningFragment();

  LayoutContext layoutContext();

  void setLayoutContext(LayoutContext layoutContext);

  StackingContext stackingContext();

  void setStackingContext(StackingContext stackingContext);

  void update();

  boolean sharesContent(ElementBox elementBox);

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
