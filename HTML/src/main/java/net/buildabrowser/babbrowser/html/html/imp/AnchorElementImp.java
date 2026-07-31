package net.buildabrowser.babbrowser.html.html.imp;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;
import net.buildabrowser.babbrowser.dom.events.PointerEvent;
import net.buildabrowser.babbrowser.html.html.AnchorElement;
import net.buildabrowser.babbrowser.html.html.util.NavUtil;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public class AnchorElementImp extends HTMLElementImp implements AnchorElement, ActivationTarget {

  public AnchorElementImp(String name, String namespace, Node parentNode) {
    super(name, namespace, parentNode);
  }

  @Override
  public void activate(PointerEvent event) {
    if (!hasAttribute("href")) return;
    String hyperlinkSuffix = null;
    // TODO: Other stuff
    UserNavigationInvolvement userInvolvement = UserNavigationInvolvement.ACTIVATION; // TODO: Proper way to get this
    // TODO: Downloads
    NavUtil.followHyperlink(
      this, hyperlinkSuffix, userInvolvement,
      event.ctrlKey());
  }
  
}
