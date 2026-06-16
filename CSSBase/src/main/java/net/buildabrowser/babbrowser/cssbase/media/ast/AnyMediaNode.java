package net.buildabrowser.babbrowser.cssbase.media.ast;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;

public record AnyMediaNode(List<MediaNode> inner) implements MediaNode {

  @Override
  public boolean resolve(MediaContext context) {
    if (inner.isEmpty()) return true;
    for (MediaNode node: inner) {
      if (node.resolve(context)) return true;
    }
    return false;
  }

  public static AnyMediaNode create(List<MediaNode> inner) {
    return new AnyMediaNode(inner);
  }

  public static AnyMediaNode create(MediaNode... inner) {
    return new AnyMediaNode(List.of(inner));
  }
  
}
