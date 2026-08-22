package net.buildabrowser.babbrowser.renderer.box.imp;

import java.util.List;

import net.buildabrowser.babbrowser.debugger.core.DebugBox;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.renderer.box.TextBox;

public class TextBoxImp extends AbstractBoxImp implements TextBox {
  
  private Text textNode;

  public TextBoxImp(Text textNode) {
    this.textNode = textNode;
  }

  @Override
  public Text textNode() {
    return this.textNode;
  }

  @Override
  public String text() {
    return textNode.data();
  }

  // Debugger stuff

  @Override
  public Text relatedNode() {
    return this.textNode;
  }

  @Override
  public List<DebugBox> childDebugBoxes() {
    return List.of();
  }

  @Override
  public DebugSnapshot snapshotDebugInfo() {
    return DebugSnapshot.builder().build();
  }

  @Override
  public DebugBoxType debugBoxType() {
    return DebugBoxType.TEXT;
  }
  
}
