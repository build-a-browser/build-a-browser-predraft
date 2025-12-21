package net.buildabrowser.babbrowser.dom.mutable.imp;

import net.buildabrowser.babbrowser.dom.mutable.MutableComment;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;

public class MutableCommentImp extends MutableNodeImp implements MutableComment {

  private final String data;

  public MutableCommentImp(String data, MutableNode parentNode) {
    super(parentNode);
    this.data = data;
  }

  @Override
  public String data() {
    return this.data;
  }

  @Override
  public String toString() {
    return this.data.toString();
  }
  
}
