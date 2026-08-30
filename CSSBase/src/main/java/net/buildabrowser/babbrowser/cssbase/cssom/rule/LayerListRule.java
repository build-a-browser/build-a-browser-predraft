package net.buildabrowser.babbrowser.cssbase.cssom.rule;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;

public class LayerListRule implements CSSRule {
  
  private final List<List<String>> layerNames;

  // Primarily exists to prevent GC
  // But might as well use it to reduce repeated parsing
  private List<CSSLayer> layers;

  public LayerListRule(List<List<String>> layerNames) {
    this.layerNames = layerNames;
  }

  public List<List<String>> layerNames() {
    return this.layerNames;
  }

  public List<CSSLayer> layers() {
    return this.layers;
  }

  public void setLayers(List<CSSLayer> layers) {
    this.layers = layers;
  }

}
