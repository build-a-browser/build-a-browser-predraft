package net.buildabrowser.babbrowser.dom.imp;

import net.buildabrowser.babbrowser.dom.Comment;

public class CommentImp extends NodeImp implements Comment {

  private final String data;

  public CommentImp(String data) {
    this.data = data;
  }

  @Override
  public String data() {
    return this.data;
  }
  
}
