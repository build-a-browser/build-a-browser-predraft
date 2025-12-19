package net.buildabrowser.babbrowser.htmlparser.token;

import net.buildabrowser.babbrowser.htmlparser.token.imp.CommentTokenImp;

public interface CommentToken {

  String data();

  void appendCodePointToData(int ch);

  static CommentToken create() {
    return new CommentTokenImp();
  }

}
