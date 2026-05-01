package net.buildabrowser.babbrowser.dom;

import net.buildabrowser.babbrowser.dom.imp.CommentImp;

public interface Comment extends Node {

  String data();

  public static Comment create(String data) {
    return new CommentImp(data);
  }

}
