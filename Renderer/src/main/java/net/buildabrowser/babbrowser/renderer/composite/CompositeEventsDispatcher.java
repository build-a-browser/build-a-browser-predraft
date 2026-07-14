package net.buildabrowser.babbrowser.renderer.composite;

import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.visibility.VisibilityValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;

public final class CompositeEventsDispatcher {
  
  private CompositeEventsDispatcher() {}

  public static EventHandlerResponse dispatchMouseEvent(
    EventContext eventContext,
    CompositeLayer rootLayer, RendererMouseEvent mouseEvent,
    float winX, float winY
  ) {
    eventContext.setPreventDefault(false);
    boolean preventDefault = interceptEvent(eventContext, rootLayer, mouseEvent, winX, winY);
    eventContext.setPreventDefault(preventDefault || eventContext.isPreventDefault());
    EventHandlerResponse eventResponse = handleMouseEvent(
      eventContext, rootLayer, mouseEvent, winX, winY);

    return eventResponse;
  }

  public static EventHandlerResponse handleMouseEvent(
    EventContext eventContext,
    CompositeLayer layer, RendererMouseEvent mouseEvent,
    float relX, float relY
  ) {
    float layerRelX = relX - layer.position().vpX();
    float layerRelY = relY - layer.position().vpY();

    EventHandlerResponse mouseEventResponse = handleMouseEventForChildren(
      eventContext, layer, mouseEvent,
      relX, relY, true);
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    mouseEventResponse = handleMouseEventForSelf(
      eventContext, layer, mouseEvent,
      layerRelX, layerRelY);
    if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;

    return handleMouseEventForChildren(
      eventContext, layer, mouseEvent,
      relX, relY, false);
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
      BoxFragment<?> fragment = entry.fragment();
      if (!EventUtil.aabb(fragment, relX, relY)) continue;

      CSSValue visibility = fragment.box().properties().get(CSSProperty.VISIBILITY);
    if (!visibility.equals(VisibilityValue.VISIBLE)) continue;

      EventHandlerResponse mouseEventResponse = fragment.withEventHandler((eh, f) -> eh.handleMouseEvent(
        eventContext, mouseEvent, f, relX, relY));
      if (!mouseEventResponse.isUnhandled()) return mouseEventResponse;
    }

    return EventHandlerResponse.UNHANDLED;
  }

  private static boolean interceptEvent(
    EventContext eventContext,
    CompositeLayer layer,
    RendererMouseEvent mouseEvent,
    float relX, float relY
  ) {
    float layerRelX = relX - layer.position().vpX();
    float layerRelY = relY - layer.position().vpY();

    CompositeLayerEntry entries = layer.entries();
    ScrollBoxFragment scrollBoxFragment =
      entries != null
      && entries.next() == null
      && entries.fragment() instanceof ScrollBoxFragment scrollBoxFragment_
      ? scrollBoxFragment_ : null;
    float scrollX = scrollBoxFragment != null ? scrollBoxFragment.scrollX() : 0;
    float scrollY = scrollBoxFragment != null ? scrollBoxFragment.scrollY() : 0;
    
    for (CompositeLayer child: layer.childLayers()) {
      interceptEvent(
        eventContext, child, mouseEvent,
        relX, relY);
    }

    // TODO: Why is entries sometimes null??
    StackingContext layerContext = layer.entries() == null ? null :
      layer.entries().fragment().box().stackingContext();
      
    boolean preventedDefault = false;
    for (ElementBox observerBox: eventContext.eventObservers()) {
      StackingContext observerContext = observerBox.stackingContext();
      if (observerContext != layerContext) continue;

      // TODO: Position fragment is not reliable
      BoxFragment<?> observerFragment = observerBox.positioningFragment();
      // TODO: This can happen if an event is handled mid-layout (it is racey)
      // But we can't sync, the whole point of not being on the event loop is to handle
      // things like scrollbars while the event loop is busy
      if (observerFragment == null) return false;
      
      float childRelX =
        layerRelX - observerFragment.layerX(Measurement.BORDER) + scrollX;
      float childRelY =
        layerRelY - observerFragment.layerY(Measurement.BORDER) + scrollY;

      preventedDefault |= observerFragment.withEventHandler((eh, f) -> eh.interceptMouseEvent(
        eventContext, mouseEvent, f,
        childRelX, childRelY));
    }

    return preventedDefault;
  }

}
