package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugContext;
import net.buildabrowser.babbrowser.debugger.core.DebugLayer;
import net.buildabrowser.babbrowser.debugger.core.DebugObject;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class HTMLDebugContext implements DebugContext {

  private final HTMLDocument document;
  private final DocumentBox documentBox;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLDebugContext(
    HTMLDocument document,
    DocumentBox documentBox,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.document = document;
    this.documentBox = documentBox;
    this.renderContexts = renderContexts;
  }

  @Override
  public DebugObject debugObjectForNode(Node node) {
    return new HTMLNodeDebugObject(node, renderContexts);
  }

  @Override
  public Node rootNode() {
    return document;
  }

  @Override
  public DebugBox rootBox() {
    return this.documentBox;
  }

  @Override
  public DebugLayer rootLayer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'rootLayer'");
  }

}
