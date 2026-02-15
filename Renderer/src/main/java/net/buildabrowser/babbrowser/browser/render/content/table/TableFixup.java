package net.buildabrowser.babbrowser.browser.render.content.table;

import java.util.ListIterator;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
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
    ListIterator<Box> childIt = elementBox.childBoxes();
    while (childIt.hasNext()) {
      Box childBox = childIt.next();
      if (
        childBox instanceof ElementBox childElementBox
        && childElementBox.activeStyles().innerDisplayValue()
          .equals(InnerDisplayValue.TABLE_COLUMN)
      ) {
        removeIrrelevantBoxes(childElementBox);
      } else {
        childIt.remove();
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
    ElementBoxIterator childIt = elementBox.childBoxes();
    ElementBoxIterator whitespaceStartIt = null;
    while (childIt.hasNext()) {
      Box child = childIt.next();
      
      if (
        child instanceof ElementBox childElementBox
        && TableBoxUtil.isTableNonRoot(childElementBox)
      ) {
        if (!isBadPredecessor && whitespaceStartIt != null) {
          whitespaceStartIt.remove();
          while (whitespaceStartIt.hasNext() && whitespaceStartIt.next() != child) {
            whitespaceStartIt.remove();
          }
        }

        isBadPredecessor = false;
        whitespaceStartIt = null;
      } else if (
        child instanceof TextBox childTextBox
        && childTextBox.text().isBlank()
      ) {
        whitespaceStartIt = whitespaceStartIt != null ? whitespaceStartIt : childIt.clone();
      } else {
        isBadPredecessor = true;
      }
    }

    if (!isBadPredecessor && whitespaceStartIt != null) {
      whitespaceStartIt.remove();
      while (whitespaceStartIt.hasNext()) {
        whitespaceStartIt.next();
        whitespaceStartIt.remove();
      }
    }
  }

  private static void generateMissingTableChildWrappers(ElementBox elementBox) {
    ElementBox currentFixupWrapper = null;
    ListIterator<Box> childIt = elementBox.childBoxes();
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
        childIt.set(currentFixupWrapper);
        currentFixupWrapper.addChild(childBox);
      }
    }

    // Table rows can be generated during the above, though ideally in the future we could do
    // this in the same scan.
    findAndFixupTableRowChildren(elementBox, true);
  }

  private static void generateMissingRowGroupChildWrappers(ElementBox elementBox) {
    ElementBox currentFixupWrapper = null;
    ListIterator<Box> childIt = elementBox.childBoxes();
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
        currentFixupWrapper.addChild(childBox);
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
    ListIterator<Box> childIt = elementBox.childBoxes();
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
        currentFixupWrapper.addChild(childBox);
      }
    }
  }

}
