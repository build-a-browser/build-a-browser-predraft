package net.buildabrowser.babbrowser.html.navigation;

public enum NavigationHistoryBehavior {

  AUTO(null),
  PUSH(NavigationType.PUSH),
  REPLACE(NavigationType.REPLACE);

  private final NavigationType navigationType;

  private NavigationHistoryBehavior(
    NavigationType navigationType
  ) {
    this.navigationType = navigationType;
  }

  public NavigationType toNavigationType() {
    if (this.navigationType == null) {
      throw new IllegalStateException(
        "NavigationHistoryBehavior is AUTO during conversion to NavigationType");
    }

    return this.navigationType;
  }

}
