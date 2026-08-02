package net.buildabrowser.babbrowser.renderer.content.common;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.isBlank;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.box.TextBox;

public final class GenericFlexibleFixup {
  
  private GenericFlexibleFixup() {}

  public static void fixupChildren(ElementBox rootBox) {
    ElementBoxIterator childIt = rootBox.childBoxes();
    ElementBox anonymousBox = null;
    boolean isOnlyWhitespace = true;
    boolean didInsertBox = false;
    while (childIt.hasNext()) {
      switch (childIt.next()) {
        case ElementBox _1 -> {
          anonymousBox = null;
          didInsertBox = false;
        }
        case TextBox textBox -> {
          if (anonymousBox == null) {
            isOnlyWhitespace = true;
            // It's actually flex-level, but this flag has no effect regardless
            anonymousBox = ElementBox.createAnonymous(rootBox, BoxLevel.BLOCK_LEVEL);
          }
          isOnlyWhitespace = isOnlyWhitespace && isBlank(textBox.text()); // TODO: Proper HTML whitespace
          childIt.remove();
          anonymousBox.addChild(textBox);
        }
        default -> throw new UnsupportedOperationException("Don't know how to handle this type of box!");
      }

      if (
        anonymousBox != null
        && !isOnlyWhitespace
        && !didInsertBox
      ) {
        childIt.add(anonymousBox);
        didInsertBox = true;
      }
    }

    rootBox.sortChildren((a, b) -> Integer.compare(orderOf(a), orderOf(b)));
  }

  private static int orderOf(Box box) {
    assert box instanceof ElementBox;
    return ((OrderValue) ((ElementBox) box).properties().get(CSSProperty.ORDER)).order();
  }

}
