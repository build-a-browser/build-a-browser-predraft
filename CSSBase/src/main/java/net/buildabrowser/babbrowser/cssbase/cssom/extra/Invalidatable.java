package net.buildabrowser.babbrowser.cssbase.cssom.extra;

public interface Invalidatable {
  
  void invalidate(InvalidationLevel invalidationLevel);

  void validate();

}
