package net.buildabrowser.babbrowser.dom.mutable.imp;

import net.buildabrowser.babbrowser.dom.mutable.MutableComment;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;

public class MutableCommentImp extends MutableNodeImp implements MutableComment {

  private final String data;
  private final MutableDocument ownerDocument;

  public MutableCommentImp(String data, MutableDocument ownerDocument) {
    this.data = data;
    this.ownerDocument = ownerDocument;
  }

  @Override
  public MutableDocument ownerDocument() {
    return this.ownerDocument;
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
