package net.buildabrowser.babbrowser.htmlparser.token;

import net.buildabrowser.babbrowser.htmlparser.token.imp.DoctypeTokenImp;

public interface DoctypeToken {
  
  void setForceQuirks(boolean forceQuirks);

  boolean forceQuirks();

  String name();

  void appendCodePointToName(int ch);

  static DoctypeToken create() {
    return new DoctypeTokenImp();
  }

}
