package net.buildabrowser.babbrowser.dom.events;

import net.buildabrowser.babbrowser.dom.algo.ActivationTarget;

public final class EventDispatcher {
  
  private EventDispatcher() {}

  public static void dispatch(Event event, EventTarget target) {
    ActivationTarget activationTarget = null;
    // TODO: A ton of steps and stuff
    boolean isActivationEvent = event instanceof MouseEvent && event.type().equals("click");
    if (isActivationEvent && target instanceof ActivationTarget newActivationTarget) {
      activationTarget = newActivationTarget;
    }

    EventTarget parent = target.getTheParent();
    while (parent != null) {
      target = parent;
      if (
        isActivationEvent
        && activationTarget == null
        && parent instanceof ActivationTarget newActivationTarget
      ) {
        activationTarget = newActivationTarget;
      }

      // Once some future steps are implemented, this may be null at this point
      if (parent != null) {
        parent = parent.getTheParent();
      }
    }

    if (activationTarget != null) {
      // TODO: Check cancelled flag
      activationTarget.activate((PointerEvent) event);
    }
  }

}
