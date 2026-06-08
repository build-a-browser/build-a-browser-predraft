package net.buildabrowser.babbrowser.cssbase.media.ast;

public record NotMediaNode(MediaNode inner) implements MediaNode {

  public static NotMediaNode create(MediaNode inner) {
    return new NotMediaNode(inner);
  }
  
}
