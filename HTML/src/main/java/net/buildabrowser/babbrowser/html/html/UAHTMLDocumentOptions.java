package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;

public record UAHTMLDocumentOptions(
  DocumentChangeListener changeListener,
  DocumentRenderer renderer
) {
  
}
