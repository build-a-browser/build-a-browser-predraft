package net.buildabrowser.babbrowser.cssbase.media.ast;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.media.MediaContext;

public record AndMediaNode(List<MediaNode> inner) implements MediaNode {

  @Override
  public boolean resolve(MediaContext context) {
    boolean allMatched = true;
    for (MediaNode node: inner) {
      allMatched &= node.resolve(context);
    }
    return allMatched;
  }

  public static AndMediaNode create(List<MediaNode> inner) {
    return new AndMediaNode(inner);
  }

  public static AndMediaNode create(MediaNode... inner) {
    return new AndMediaNode(List.of(inner));
  }
  
}
