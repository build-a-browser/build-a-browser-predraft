package net.buildabrowser.babbrowser.html.util;

import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.html.navigation.UserNavigationInvolvement;

public final class HTMLEventUtil {
  
  private HTMLEventUtil() {}

  public static UserNavigationInvolvement userNavigationInvolvement(Event event) {
    // TODO: Properly implement
    return UserNavigationInvolvement.ACTIVATION;
  }

}
