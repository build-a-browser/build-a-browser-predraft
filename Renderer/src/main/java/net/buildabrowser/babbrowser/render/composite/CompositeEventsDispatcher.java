package net.buildabrowser.babbrowser.render.composite;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollBox;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.event.EventContext;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.render.event.EventUtil;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public final class CompositeEventsDispatcher {
  
  private CompositeEventsDispatcher() {}

  public static void dispatchMouseEvent(
    EventContext eventContext,
    CompositeLayer rootLayer, RendererMouseEvent mouseEvent,
    float winX, float winY
  ) {
    EventHandlerResponse eventResponse = handleMouseEvent(
      eventContext, rootLayer, mouseEvent, winX, winY);
    boolean preventedDefault = eventResponse.equals(EventHandlerResponse.HANDLED);
    observeEvent(eventContext, rootLayer, mouseEvent, winX, winY, preventedDefault);
  }

  public static EventHandlerResponse handleMouseEvent(
    EventContext eventContext,
    CompositeLayer layer, RendererMouseEvent mouseEvent,
    float relX, float relY
  ) {
    TranslatedRel translatedRel = translateRel(layer, relX, relY);
    EventHandlerResponse mouseEventResponse = handleMouseEventForChildren(
      eventContext, layer, mouseEvent,
      translatedRel.childRelX(), translatedRel.childRelY(), true);
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    mouseEventResponse = handleMouseEventForSelf(
      eventContext, layer, mouseEvent,
      translatedRel.relX(), translatedRel.relY());
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    return handleMouseEventForChildren(
      eventContext, layer, mouseEvent,
      translatedRel.childRelX(), translatedRel.childRelY(), false);
  }

  private static EventHandlerResponse handleMouseEventForChildren(
    EventContext eventContext,
    CompositeLayer layer, RendererMouseEvent mouseEvent,
    float relX, float relY,
    boolean isHigherHalf
  ) {
    List<CompositeLayer> childLayers = layer.childLayers();
    ListIterator<CompositeLayer> childIt = childLayers.listIterator(childLayers.size());
    while (childIt.hasPrevious()) {
      CompositeLayer childLayer = childIt.previous();
      if (childLayer.zIndex() < 0 == isHigherHalf) continue;
      EventHandlerResponse mouseEventResponse = handleMouseEvent(
        eventContext, childLayer, mouseEvent, relX, relY);
      if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;
    }

    return EventHandlerResponse.UNHANDLED;
  }

  private static EventHandlerResponse handleMouseEventForSelf(
    EventContext eventContext,
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
      if (!EventUtil.aabb(fragment, relX, relY)) continue;

      EventHandler eventHandler = fragment.eventHandler();
      EventHandlerResponse mouseEventResponse = eventHandler.handleMouseEvent(
        eventContext, mouseEvent, fragment, relX, relY);
      if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;
    }

    return EventHandlerResponse.UNHANDLED;
  }

  private static void observeEvent(
    EventContext eventContext,
    CompositeLayer layer,
    RendererMouseEvent mouseEvent,
    float relX, float relY,
    boolean preventedDefault
  ) {
    TranslatedRel translatedRel = translateRel(layer, relX, relY);
    
    for (CompositeLayer child: layer.childLayers()) {
      observeEvent(
        eventContext, child, mouseEvent,
        translatedRel.childRelX(), translatedRel.childRelY(),
        preventedDefault);
    }

    // TODO: Why is entries sometimes null??
    StackingContext layerContext = layer.entries() == null ? null :
      layer.entries().fragment().box().stackingContext();
    for (ElementBox observerBox: eventContext.eventObservers()) {
      StackingContext observerContext = observerBox.stackingContext();
      if (observerContext != layerContext) continue;

      float childRelX = observerBox instanceof ScrollBox ?
        translatedRel.relX() : translatedRel.childRelX();
      float childRelY = observerBox instanceof ScrollBox ?
        translatedRel.relY() : translatedRel.childRelY();
      // TODO: Position fragment is not reliable
      BoxFragment observerFragment = observerBox.positioningFragment();
      childRelX -= observerFragment.layerX(Measurement.BORDER);
      childRelY -= observerFragment.layerY(Measurement.BORDER);

      observerBox.content().eventHandler().observeMouseEvent(
        eventContext, mouseEvent, observerFragment,
        childRelX, childRelY, preventedDefault);
    }
  }

  private static TranslatedRel translateRel(CompositeLayer layer, float relX, float relY) {
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
      && entries.fragment() instanceof ScrollBoxFragment scrollBoxFragment
    ) {
      childRelX += scrollBoxFragment.box().scrollX();
      childRelY += scrollBoxFragment.box().scrollY();
    }

    return new TranslatedRel(relX, relY, childRelX, childRelY);
  }

  private record TranslatedRel(
    float relX, float relY,
    float childRelX, float childRelY
  ) {}

}
