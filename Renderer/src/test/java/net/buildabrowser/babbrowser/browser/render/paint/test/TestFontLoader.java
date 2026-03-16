package net.buildabrowser.babbrowser.browser.render.paint.test;

import net.buildabrowser.babbrowser.browser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedFont;

public class TestFontLoader implements FontLoader {

  private final LoadedFont testFont;

  public TestFontLoader(LoadedFont testFont) {
    this.testFont = testFont;
  }

  @Override
  public FontFamily monospace() {
    return null;
  }

  @Override
  public FontFamily serif() {
    return null;
  }

  @Override
  public FontFamily sansSerif() {
    return null;
  }

  @Override
  public FontFamily named(String name) {
    return null;
  }

  @Override
  public LoadedFont load(FontOptions options) {
    return testFont;
  }

}
