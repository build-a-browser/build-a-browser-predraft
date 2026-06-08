package net.buildabrowser.babbrowser.cssbase.media.ast;

import java.util.List;

public record AnyMediaNode(List<MediaNode> inner) implements MediaNode {

  public static AnyMediaNode create(List<MediaNode> inner) {
    return new AnyMediaNode(inner);
  }

  public static AnyMediaNode create(MediaNode... inner) {
    return new AnyMediaNode(List.of(inner));
  }
  
}
