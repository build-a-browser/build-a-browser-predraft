package net.buildabrowser.babbrowser.browser.net.imp;

import net.buildabrowser.babbrowser.cookies.PublicSuffixList;

public class PublicSuffixListImp implements PublicSuffixList {

  @Override
  public boolean contains(String suffix) {
    return false;
  }
  
}
