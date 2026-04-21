package net.buildabrowser.babbrowser.render.composite;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;

public final class CompositeEventsDispatcher {
  
  private CompositeEventsDispatcher() {}

  public static EventHandlerResponse handleMouseEvent(
    CompositeLayer layer, RendererMouseEvent mouseEvent,
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

    float childRelX = relX;
    float childRelY = relY;

    CompositeLayerEntry entries = layer.entries();
    if (
      entries != null
      && entries.next() == null
      && entries.fragment() instanceof ScrollBoxFragment scrollBox
    ) {
      childRelX += scrollBox.scrollX();
      childRelY += scrollBox.scrollY();
    }
    EventHandlerResponse mouseEventResponse = handleMouseEventForChildren(layer, mouseEvent, childRelX, childRelY, true);
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    mouseEventResponse = handleMouseEventForSelf(layer, mouseEvent, relX, relY);
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    return handleMouseEventForChildren(layer, mouseEvent, childRelX, childRelY, false);
  }

  private static EventHandlerResponse handleMouseEventForChildren(
    CompositeLayer layer, RendererMouseEvent mouseEvent,
    float relX, float relY,
    boolean isHigherHalf
  ) {
    List<CompositeLayer> childLayers = layer.childLayers();
    ListIterator<CompositeLayer> childIt = childLayers.listIterator(childLayers.size());
    while (childIt.hasPrevious()) {
      CompositeLayer childLayer = childIt.previous();
      if (childLayer.zIndex() < 0 == isHigherHalf) continue;
      EventHandlerResponse mouseEventResponse = handleMouseEvent(childLayer, mouseEvent, relX, relY);
      if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;
    }

    return EventHandlerResponse.UNHANDLED;
  }

  private static EventHandlerResponse handleMouseEventForSelf(
    CompositeLayer layer,
    RendererMouseEvent mouseEvent,
    float relX, float relY
  ) {
    CompositeLayerEntry entries = layer.entries();
    // Doesn't support backward iteration by default, we'll use a short-lived list for now
    List<CompositeLayerEntry> entriesList = IntrusiveList.toList(entries);
    ListIterator<CompositeLayerEntry> childIt = entriesList.listIterator(entriesList.size());
    while (childIt.hasPrevious()) {
      CompositeLayerEntry entry = childIt.previous();
      BoxFragment fragment = entry.fragment();
      float childRelX = relX - entry.offsetX();
      float childRelY = relY - entry.offsetY();
      if (!EventUtil.aabbZeroAdjusted(fragment, childRelX, childRelY)) continue;

      EventHandler eventHandler = fragment.eventHandler();
      EventHandlerResponse mouseEventResponse = eventHandler.handleMouseEvent(
        mouseEvent, fragment, childRelX, childRelY);
      if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;
    }

    return EventHandlerResponse.UNHANDLED;
  }

}
