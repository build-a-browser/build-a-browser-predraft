package net.buildabrowser.babbrowser.render.paint;

import java.util.List;

public interface FontLoader {

  FontFamily monospace();

  FontFamily serif();

  FontFamily sansSerif();

  FontFamily named(String name);

  LoadedFont load(FontOptions options);

  record FontOptions(List<FontFamily> families, float size, int weight) {}
  
  interface FontFamily {}

}
