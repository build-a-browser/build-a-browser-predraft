package net.buildabrowser.babbrowser.browser.render.content.table;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.TextBox;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class TableFixup {

  private TableFixup() {}

  public static void adjustTableBox(ElementBox elementBox) {
    removeIrrelevantBoxes(elementBox);
    generateMissingTableChildWrappers(elementBox);
    // TODO: Finish all that
  }

  private static void removeIrrelevantBoxes(ElementBox elementBox) {
    InnerDisplayValue displayValue = elementBox.activeStyles().innerDisplayValue();
    if (displayValue.equals(InnerDisplayValue.TABLE_COLUMN)) {
      elementBox.clearChildren();
    } else if (displayValue.equals(InnerDisplayValue.TABLE_COLUMN_GROUP)) {
      removeIrrelevantBoxesColumn(elementBox);
    } else {
      recurseRemoveIrrelevantBoxes(elementBox);
      removeWhitespace(elementBox);
    }
  }

  private static void removeIrrelevantBoxesColumn(ElementBox elementBox) {
    List<Box> children = elementBox.childBoxes();
    for (int i = 0; i < children.size(); i++) {
      Box childBox = children.get(i);
      if (
        childBox instanceof ElementBox childElementBox
        && childElementBox.activeStyles().innerDisplayValue()
          .equals(InnerDisplayValue.TABLE_COLUMN)
      ) {
        removeIrrelevantBoxes(childElementBox);
      } else {
        elementBox.removeChild(childBox);
        i--;
      }
    }
  }

  private static void recurseRemoveIrrelevantBoxes(ElementBox elementBox) {
    for (Box childBox: elementBox.childBoxes()) {
      if (
        childBox instanceof ElementBox childElementBox
        && TableBoxUtil.isTableNonRoot(childElementBox)
      ) {
        removeIrrelevantBoxes(childElementBox);
      }
    }
  }

  private static void removeWhitespace(ElementBox elementBox) {
    boolean isBadPredecessor = false;
    int whitespaceStart = -1;
    List<Box> children = elementBox.childBoxes();
    for (int i = 0; i < children.size(); i++) {
      Box child = children.get(i);
      if (
        child instanceof ElementBox childElementBox
        && TableBoxUtil.isTableNonRoot(childElementBox)
      ) {
        if (!isBadPredecessor && whitespaceStart != -1) {
          while (i > whitespaceStart) {
            elementBox.removeChild(whitespaceStart);
            i--;
          }
        }

        isBadPredecessor = false;
        whitespaceStart = -1;
      } else if (
        child instanceof TextBox childTextBox
        && childTextBox.text().isBlank()
      ) {
        whitespaceStart = whitespaceStart != -1 ? whitespaceStart : i;
      } else {
        isBadPredecessor = true;
      }
    }

    if (!isBadPredecessor && whitespaceStart != -1) {
      while (whitespaceStart < children.size()) {
        elementBox.removeChild(whitespaceStart);
      }
    }
  }

  private static void generateMissingTableChildWrappers(ElementBox elementBox) {
    ElementBox currentFixupWrapper = null;
    ListIterator<Box> childIt = elementBox.childBoxes().listIterator();
    while (childIt.hasNext()) {
      Box childBox = childIt.next();
      if (TableBoxUtil.isProperTableChild(childBox)) {
        if (TableBoxUtil.isTableRowGroup(childBox)) {
          generateMissingRowGroupChildWrappers((ElementBox) childBox);
        }
        currentFixupWrapper = null;
      } else if (currentFixupWrapper != null) {
        childIt.remove();
        currentFixupWrapper.addChild(childBox);
      } else {
        ActiveStyles anonStyles = ActiveStyles.create(elementBox.activeStyles());
        anonStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
          OuterDisplayValue.TABLE_ROW, InnerDisplayValue.TABLE_ROW));
        currentFixupWrapper = ElementBox.createAnonymous(anonStyles, elementBox, BoxLevel.INLINE_LEVEL);
        currentFixupWrapper.addChild(childBox);
        childIt.set(currentFixupWrapper);
      }
    }

    // Table rows can be generated during the above, though ideally in the future we could do
    // this in the same scan.
    findAndFixupTableRowChildren(elementBox, true);
  }

  private static void generateMissingRowGroupChildWrappers(ElementBox elementBox) {
    ElementBox currentFixupWrapper = null;
    ListIterator<Box> childIt = elementBox.childBoxes().listIterator();
    while (childIt.hasNext()) {
      Box childBox = childIt.next();
      if (TableBoxUtil.isTableRow(childBox)) {
        currentFixupWrapper = null;
      } else if (currentFixupWrapper != null) {
        childIt.remove();
        currentFixupWrapper.addChild(childBox);
      } else {
        ActiveStyles anonStyles = ActiveStyles.create(elementBox.activeStyles());
        anonStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
          OuterDisplayValue.TABLE_ROW, InnerDisplayValue.TABLE_ROW));
        currentFixupWrapper = ElementBox.createAnonymous(anonStyles, elementBox, BoxLevel.BLOCK_LEVEL);
        childIt.set(currentFixupWrapper);
      }
    }
  }

  private static void findAndFixupTableRowChildren(ElementBox elementBox, boolean allowGroups) {
    for (Box childBox: elementBox.childBoxes()) {
      if (!(childBox instanceof ElementBox childElementBox)) continue;
      if (TableBoxUtil.isTableRow(childElementBox)) {
        generateMissingRowChildWrappers(childElementBox);
      } else if (allowGroups && TableBoxUtil.isTableRowGroup(childElementBox)) {
        findAndFixupTableRowChildren(childElementBox, false);
      }
    }
  }

  private static void generateMissingRowChildWrappers(ElementBox elementBox) {
    ElementBox currentFixupWrapper = null;
    ListIterator<Box> childIt = elementBox.childBoxes().listIterator();
    while (childIt.hasNext()) {
      Box childBox = childIt.next();
      if (TableBoxUtil.isTableCell(childBox)) {
        currentFixupWrapper = null;
      } else if (currentFixupWrapper != null) {
        childIt.remove();
        currentFixupWrapper.addChild(childBox);
      } else {
        ActiveStyles anonStyles = ActiveStyles.create(elementBox.activeStyles());
        anonStyles.setProperty(CSSProperty.DISPLAY, DisplayValue.create(
          OuterDisplayValue.TABLE_CELL, InnerDisplayValue.FLOW_ROOT));
        currentFixupWrapper = ElementBox.createAnonymous(anonStyles, elementBox, BoxLevel.BLOCK_LEVEL);
        childIt.set(currentFixupWrapper);
      }
    }
  }

}
