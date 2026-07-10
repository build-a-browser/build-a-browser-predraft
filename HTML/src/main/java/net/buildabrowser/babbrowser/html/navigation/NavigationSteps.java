package net.buildabrowser.babbrowser.html.navigation;

public record NavigationSteps(
  Runnable algorithm,
  Navigable targetNavigable
) {
  
  public boolean synchronous() {
    return targetNavigable != null;
  }

}
