package net.buildabrowser.babbrowser.html.html;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public record UAHTMLDocumentOptions(
  DocumentChangeListener changeListener,
  DocumentRenderer renderer,
  Consumer<InvalidationLevel> onInvalidate
) {
  
}
