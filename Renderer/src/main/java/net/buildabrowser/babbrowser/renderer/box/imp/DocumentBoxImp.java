package net.buildabrowser.babbrowser.renderer.box.imp;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.renderer.box.DocumentBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public class DocumentBoxImp extends AbstractBoxImp implements DocumentBox {

  private final HTMLDocument document;

  private ElementBox childBox;

  public DocumentBoxImp(HTMLDocument document) {
    this.document = document;
  }

  @Override
  public HTMLDocument document() {
    return this.document;
  }

  @Override
  public ElementBox child() {
    return this.childBox;
  }

  @Override
  public void setChild(ElementBox child) {
    this.childBox = child;
  }

  // Debugger stuff

  @Override
  public HTMLDocument relatedNode() {
    return this.document;
  }

  @Override
  public List<DebugBox> childDebugBoxes() {
    return List.of(childBox);
  }

  @Override
  public DebugSnapshot snapshotDebugInfo() {
    return DebugSnapshot.builder().build();
  }

  @Override
  public DebugBoxType debugBoxType() {
    return DebugBoxType.DOCUMENT;
  }
  
}
