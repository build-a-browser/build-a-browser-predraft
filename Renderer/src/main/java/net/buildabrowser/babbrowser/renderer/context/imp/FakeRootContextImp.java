package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public class FakeRootContextImp extends RenderContextImp {

  private final DocumentBox documentBox;
  private final ElementBox wrapperBox;
  private final ScrollBox rootScrollBox;

  private ElementBox htmlBox;

  public FakeRootContextImp(
    short slotFamilyId,
    DocumentBox documentBox
  ) {
    super(slotFamilyId);
    this.documentBox = documentBox;
    this.computedStyles = ActiveStyles.unparentedStyles(ActiveStyles.create());
    this.rootScrollBox = new ScrollBox(this, documentBox, BoxLevel.BLOCK_LEVEL);
    this.wrapperBox = ElementBox.createAnonymous(properties(), rootScrollBox, BoxLevel.BLOCK_LEVEL);
    rootScrollBox.addChild(wrapperBox);
  }

  // Be careful not to invalidate box during regeneration
  @Override
  public ActiveStyles regenerateStyles(StyleCache styleCache, ActiveStyles refStyles) {
    PropertyContainer oldStyles = this.computedStyles;
    ActiveStyles activeStyles = ActiveStyles.create();

    if (htmlBox != null) {
      updatePaintProperties(activeStyles);
      activeStyles.setProperty(CSSProperty.OVERFLOW_X, CompositeLayerUtil.adjustHTMLOverflowValue(
        htmlBox.context(), CSSProperty.OVERFLOW_X));
      activeStyles.setProperty(CSSProperty.OVERFLOW_Y, CompositeLayerUtil.adjustHTMLOverflowValue(
        htmlBox.context(), CSSProperty.OVERFLOW_Y));
    }

    this.computedStyles = ActiveStyles.unparentedStyles(activeStyles);
    
    invalidate(changedPropertyInvalidationLevel(oldStyles, this.computedStyles));

    return refStyles;
  }

  private void updatePaintProperties(ActiveStyles activeStyles) {
    PropertyContainer refBgProperties = ElementBackgroundPainter.inheritsBodyBackground(htmlBox.element(), htmlBox) ?
      scanBodyProperties(htmlBox) :
      htmlBox.properties();

    for (CSSProperty backgroundProperty: CSSProperty.BACKGROUND.getExpansions()) {
      activeStyles.setProperty(backgroundProperty,
        refBgProperties.get(backgroundProperty));
    }
  }

  @Override
  public HTMLElement element() {
    if (htmlBox == null) return null;
    return htmlBox.element();
  }

  @Override
  public ElementBox box() {
    return this.rootScrollBox;
  }

  @Override
  public void setBox(ElementBox box) {
    throw new UnsupportedOperationException("Unimplemented method 'setBox'");
  }

  @Override
  public void invalidate(short invalidationLevel) {
    if ((invalidationLevel & invalidationLevel()) != invalidationLevel) {
      if (
        documentBox.document() instanceof HTMLDocument document
        && document.renderer() != null
      ) {
        document.renderer().onDocumentInvalidated(invalidationLevel);
      }
    }

    super.invalidate(invalidationLevel);
  }

  @Override
  public void validate() {
    if (htmlBox != null) {
      htmlBox.context().validate();
    }
    super.validate();
  }

  public ElementBox wrapperBox() {
    return this.wrapperBox;
  }

  public void replaceChild(ElementBox child) {
    this.htmlBox = child;
    wrapperBox.startOverwrite();
    wrapperBox.includeChild(child);
    wrapperBox.endOverwrite();

    // TODO: This needs to be called after boxing because some of the properties
    // scan the child. But some of our properties invalidate BOX. Will this ever
    // cause a loop?
    regenerateStyles(null, null);
  }

  private static PropertyContainer scanBodyProperties(ElementBox box) {
    // TODO: Would it be better to get boxes via DOM mappings to ignore any wrapper boxes?
    if (box instanceof ScrollBox) {
      ElementBoxIterator childIt = box.childBoxes();
      if (
        childIt.hasNext()
        && childIt.next() instanceof ElementBox elBox
      ) {
        box = elBox;
      } else {
        return box.properties();
      }
    }

    if (box.element() == null) {
      return box.properties();
    }

    Node childNode = box.element().firstChild();
    while (childNode != null) {
      if (
        childNode instanceof HTMLElement childEl
        && childEl.name().equals("body") // TODO: instanceof
      ) {
        short familyId = box.context().familyId();
        ElementContext context = (ElementContext) SlotItem.getExistingById(childEl, familyId);
        return context.properties();
      }
      childNode = childNode.nextSibling();
    }

    return box.properties();
  }
  
}
