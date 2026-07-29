package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;

public class HTMLTextAreaElementImp extends HTMLElementImp implements HTMLTextAreaElement, ActivationTarget {

  private String value = "";
  private HTMLFormElement formOwner;

  public HTMLTextAreaElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public boolean disabled() {
    return hasAttribute("disabled");
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value == null ? "" : value;
    // TODO: There might be cases where it should invalidate layout
    // instead - should delegate to the renderer
    invalidate(InvalidationLevel.PAINT);
  }

  @Override
  public void activate(PointerEvent event) {
    // TODO: Proper way to block event
    if (disabled()) return;
    
    
  }

  @Override
  public HTMLFormElement form() {
    return this.formOwner;
  }

  @Override
  public void setFormOwner(HTMLFormElement formOwner) {
    this.formOwner = formOwner;
  }
  
}
