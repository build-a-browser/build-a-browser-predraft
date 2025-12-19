package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableCommentImp;

public interface MutableComment extends MutableNode, Comment {

  static MutableComment create(String data, MutableDocument ownerDocument) {
    return new MutableCommentImp(data, ownerDocument);
  }
  
}
