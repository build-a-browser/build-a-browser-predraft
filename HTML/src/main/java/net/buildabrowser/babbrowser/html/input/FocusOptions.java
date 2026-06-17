package net.buildabrowser.babbrowser.html.input;

public class FocusOptions {
  
  public boolean preventScroll = false;
  public boolean focusVisible;

  public static FocusOptions createDefault() {
    FocusOptions options = new FocusOptions();
    options.focusVisible = true;
    return options;
  }

}
