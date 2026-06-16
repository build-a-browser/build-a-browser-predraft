package net.buildabrowser.babbrowser.cssbase.media.ast;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;

public record NotMediaNode(MediaNode inner) implements MediaNode {

  @Override
  public boolean resolve(MediaContext context) {
    return !inner.resolve(context);
  }

  public static NotMediaNode create(MediaNode inner) {
    return new NotMediaNode(inner);
  }
  
}
