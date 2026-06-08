package net.buildabrowser.babbrowser.cssbase.media.ast;

import java.util.List;

public record AndMediaNode(List<MediaNode> inner) implements MediaNode {

  public static AndMediaNode create(List<MediaNode> inner) {
    return new AndMediaNode(inner);
  }

  public static AndMediaNode create(MediaNode... inner) {
    return new AndMediaNode(List.of(inner));
  }
  
}
