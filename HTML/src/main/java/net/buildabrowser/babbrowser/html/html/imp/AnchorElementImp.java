package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.html.AnchorElement;

public class AnchorElementImp extends HTMLElementImp implements AnchorElement, ActivationTarget {

  public AnchorElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public void activate(PointerEvent event) {
    System.out.println("Activated an anchor whose href is " + attributes().get("href"));
  }
  
}
