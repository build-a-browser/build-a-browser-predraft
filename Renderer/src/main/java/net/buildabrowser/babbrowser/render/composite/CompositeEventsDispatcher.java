package net.buildabrowser.babbrowser.render.composite;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent;

public final class CompositeEventsDispatcher {
  
  private CompositeEventsDispatcher() {}

  public static boolean handleMouseEvent(
    CompositeLayer layer, MouseEvent mouseEvent,
    float relX, float relY
  ) {
    switch (layer.positioning()) {
      case STATIC, RELATIVE, ABSOLUTE -> {
        relX -= layer.posX();
        relY -= layer.posY();
      }
      case FIXED -> { /* TODO: Implement */ }
      case STICKY -> { /* TODO: Implement */ }
      default -> throw new IllegalArgumentException("Unexpected value: " + layer.positioning());
    }

    boolean handledMouseEvent = handleMouseEventForChildren(layer, mouseEvent, relX, relY, true);
    if (handledMouseEvent) return true;

    handledMouseEvent = handleMouseEventForSelf(layer, mouseEvent, relX, relY);
    if (handledMouseEvent) return true;

    return handleMouseEventForChildren(layer, mouseEvent, relX, relY, false);
  }

  private static boolean handleMouseEventForChildren(
    CompositeLayer layer, MouseEvent mouseEvent,
    float relX, float relY,
    boolean isHigherHalf
  ) {
    List<CompositeLayer> childLayers = layer.childLayers();
    ListIterator<CompositeLayer> childIt = childLayers.listIterator(childLayers.size());
    while (childIt.hasPrevious()) {
      CompositeLayer childLayer = childIt.previous();
      if (childLayer.zIndex() < 0 == isHigherHalf) continue;
      boolean handledMouseEvent = handleMouseEvent(childLayer, mouseEvent, relX, relY);
      if (handledMouseEvent) return true;
    }

    return false;
  }

  private static boolean handleMouseEventForSelf(
    CompositeLayer layer,
    MouseEvent mouseEvent,
    float relX, float relY
  ) {
    CompositeLayerEntry entries = layer.entries();
    // Doesn't support backward iteration by default, we'll use a short-lived list for now
    List<CompositeLayerEntry> entriesList = IntrusiveList.toList(entries);
    ListIterator<CompositeLayerEntry> childIt = entriesList.listIterator(entriesList.size());
    while (childIt.hasPrevious()) {
      CompositeLayerEntry entry = childIt.previous();
      BoxFragment fragment = entry.fragment();
      EventHandler eventHandler = fragment.box().content().eventHandler();
      boolean handledMouseEvent = eventHandler.handleMouseEvent(
        mouseEvent, fragment,
        relX - entry.offsetX(),
        relY - entry.offsetY());
      if (handledMouseEvent) return true;
    }

    return false;
  }

}
