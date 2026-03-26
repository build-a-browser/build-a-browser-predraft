package net.buildabrowser.babbrowser.html.navigation;

import net.buildabrowser.babbrowser.html.scripting.Window;

public record BrowsingContext(
  Window window // TODO: Should be an unwrappable proxy
) {
  
  public static BrowsingContext create(Window window) {
    return new BrowsingContext(window);
  }

}
