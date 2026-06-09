package net.buildabrowser.babbrowser.renderer.fragment.table;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.table.TableEventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.table.TableBoxPainter;

public class DefaultTableBoxFragment extends TableBoxFragment {

  private static final TableBoxPainter TABLE_BOX_PAINTER = new TableBoxPainter();
  private static final TableEventHandler TABLE_EVENT_HANDLER = new TableEventHandler();

  public DefaultTableBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box,
    Table table,
    TableBorderAssignment borderAssignment
  ) {
    super(
      width, height, inkWidth, inkHeight,
      box, table, borderAssignment);
  }

  @Override
  protected BoxPainter<TableBoxFragment> painter() {
    return TABLE_BOX_PAINTER;
  }

  @Override
  protected EventHandler<TableBoxFragment> eventHandler() {
    return TABLE_EVENT_HANDLER;
  }
  
}
