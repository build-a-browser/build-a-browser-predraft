package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.debugger.core.DebugContext;
import net.buildabrowser.babbrowser.debugger.core.DebugLayer;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;

public class HTMLDebugContext implements DebugContext {

  private final HTMLDocument document;

  public HTMLDebugContext(HTMLDocument document) {
    this.document = document;
  }

  @Override
  public Node rootNode() {
    return document;
  }

  @Override
  public DebugLayer rootLayer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'rootLayer'");
  }

}
