package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;

public record MediaTypeNode(String type) implements MediaNode {

  @Override
  public boolean resolve(MediaContext context) {
    return
      type.equals("all")
      || context.mediaTypes().contains(type);
  }

  public static MediaTypeNode create(String type) {
    return new MediaTypeNode(type);
  }
  
}
