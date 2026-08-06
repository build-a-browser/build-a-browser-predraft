package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public interface RenderContext extends Invalidatable, SlotItem<RenderContext> {
  
  void regenerateStyles(StyleCache styleCache);

  PropertyContainer properties();

  PropertyContainer targetedProperties(SelectorTarget target);

  InvalidationLevel invalidationLevel();

  HTMLElement element();

  ElementBox box();

}
