package net.buildabrowser.babbrowser.cssbase.layer.imp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;

public class CSSLayerImp implements CSSLayer {

  private final Map<String, WeakReference<CSSLayer>> children = new HashMap<>();
  // Set does not have indexOf
  private final List<CSSLayer> layerOrder = new ArrayList<>(1);

  private final CSSLayer parentLayer;
  private final String longName;
  private final int depth;

  public CSSLayerImp(
    CSSLayer parentLayer,
    String longName,
    int depth
  ) {
    this.parentLayer = parentLayer;
    this.longName = longName;
    this.depth = depth;
  }

  @Override
  public CSSLayer parentLayer() {
    return this.parentLayer;
  }

  @Override
  public String longName() {
    return this.longName;
  }

  @Override
  public int childIndex(CSSLayer child) {
    return layerOrder.indexOf(child);
  }

  @Override
  public CSSLayer childLayer(String name) {
    if (children.containsKey(name)) {
      CSSLayer layer = children.get(name).get();
      if (layer != null) return layer;
    }

    String childLongName = longName.isEmpty() ?
      name : longName + "." + name;
    CSSLayer layer = new CSSLayerImp(
      this, childLongName, depth + 1);
    layerOrder.add(layer);
    children.put(name, new WeakReference<>(layer));

    return layer;
  }

  @Override
  public CSSLayer createAnonymousChild() {
    CSSLayer layer = new CSSLayerImp(this, "", depth + 1);
    layerOrder.add(layer);
    return layer;
  }

  @Override
  public void resetOrder() {
    for (CSSLayer child: layerOrder) {
      child.resetOrder();
    }
    layerOrder.clear(); 
  }

  @Override
  public void markChildUse(CSSLayer child) {
    layerOrder.add(child);
  }

  @Override
  public int depth() {
    return this.depth;
  }

}
