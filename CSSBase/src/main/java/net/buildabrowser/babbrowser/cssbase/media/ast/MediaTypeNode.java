package net.buildabrowser.babbrowser.cssbase.media.ast;

public record MediaTypeNode(String type) implements MediaNode {

  public static MediaTypeNode create(String type) {
    return new MediaTypeNode(type);
  }
  
}
