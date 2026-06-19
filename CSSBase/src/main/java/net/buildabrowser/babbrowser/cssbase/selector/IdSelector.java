package net.buildabrowser.babbrowser.cssbase.selector;

public record IdSelector(String id) implements SimpleSelector {
  
  public static IdSelector create(String id) {
    return new IdSelector(id);
  }

}
