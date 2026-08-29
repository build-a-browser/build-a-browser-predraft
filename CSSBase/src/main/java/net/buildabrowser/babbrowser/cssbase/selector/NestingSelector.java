package net.buildabrowser.babbrowser.cssbase.selector;

public record NestingSelector() implements SimpleSelector {
  
  private static final NestingSelector INSTANCE = new NestingSelector();

  public static NestingSelector create() {
    return INSTANCE;
  }

  @Override
  public String serialize() {
    return "&";
  }

}
