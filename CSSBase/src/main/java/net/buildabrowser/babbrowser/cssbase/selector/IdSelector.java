package net.buildabrowser.babbrowser.cssbase.selector;

public record IdSelector(String id) implements SimpleSelector {

  @Override
  public String serialize() {
    return "#" + id;
  }
  
  public static IdSelector create(String id) {
    return new IdSelector(id);
  }

}
