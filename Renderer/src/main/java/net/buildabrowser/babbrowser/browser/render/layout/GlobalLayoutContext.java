package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;

public record GlobalLayoutContext(
  URL refURL,
  ResourceLoader resourceLoader
) {
  
}
