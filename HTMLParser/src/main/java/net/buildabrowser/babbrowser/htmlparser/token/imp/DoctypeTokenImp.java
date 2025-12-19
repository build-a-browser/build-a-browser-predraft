package net.buildabrowser.babbrowser.htmlparser.token.imp;

import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;

public class DoctypeTokenImp implements DoctypeToken {

  private final StringBuilder nameBuilder = new StringBuilder();
  
  private boolean forceQuirks;

  @Override
  public void setForceQuirks(boolean forceQuirks) {
    this.forceQuirks = forceQuirks;
  }

  @Override
  public boolean forceQuirks() {
    return this.forceQuirks;
  }

  @Override
  public String name() {
    return nameBuilder.toString();
  }

  @Override
  public void appendCodePointToName(int ch) {
    nameBuilder.append(ch);
  }
  
}
