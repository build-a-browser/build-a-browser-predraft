package net.buildabrowser.babbrowser.cssbase.cssom.rule;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;

public class LayerRule implements NestingRule {

  private final List<String> nameParts;
  private final List<CSSRule> nestedRules;
  
  private CSSLayer layer;

  public LayerRule(
    List<String> nameParts,
    List<CSSRule> innerRules
  ) {
    this.nameParts = nameParts;
    this.nestedRules = innerRules;
  }

  public List<String> nameParts() {
    return this.nameParts;
  }

  public List<CSSRule> nestedRules() {
    return this.nestedRules;
  }

  public void setLayer(CSSLayer layer) {
    this.layer = layer;
  }

  public CSSLayer layer() {
    return this.layer;
  }
  
}
