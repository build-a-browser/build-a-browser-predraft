package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public interface RenderContext extends Invalidatable, SlotItem<RenderContext> {
  
  ActiveStyles regenerateStyles(StyleCache styleCache, ActiveStyles refStyles);

  PropertyContainer properties();

  short invalidationLevel();

  HTMLElement element();

  ElementBox box();
  
  void setBox(ElementBox box);

}
