package net.buildabrowser.babbrowser.browser.dom;

public record Text(String text) implements Node {

  @Override
  public String toString() {
    return text;
  }

}
