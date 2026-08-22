package net.buildabrowser.babbrowser.debugger.swing.gui;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;

public class SwingStyleView extends JScrollPane {

  private final JPanel mainContainer;
  private final SwingStyleSegment metadataSection;
  private final SwingStyleSegment computedSection;
  private final Map<String, SegmentAndDivider> ruleSections = new LinkedHashMap<>();

  public SwingStyleView() {
    this.mainContainer = new JPanel();
    mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
    setViewportView(mainContainer);
    getVerticalScrollBar().setUnitIncrement(16);

    this.metadataSection = new SwingStyleSegment("Metadata", true);
    mainContainer.add(metadataSection);
    mainContainer.add(createDivider());

    this.computedSection = new SwingStyleSegment("Computed Styles", true);
    mainContainer.add(computedSection);
    mainContainer.add(createDivider());
  }

  public void updateStyles(
    DebugSnapshot snapshot,
    PropertyContainer computedStyles,
    List<WeightedStyleRule> styleRules
  ) {
    if (computedStyles == null || styleRules == null) return;

    updateMetaDataSection(snapshot);
    updateComputedStylesSection(computedStyles);

    Set<String> activeRuleKeys = new HashSet<>();
    for (WeightedStyleRule styleRule: styleRules) {
      addOrUpdateStyleRuleSection(styleRule, activeRuleKeys);
    }

    removeObsoleteEntries(activeRuleKeys);
  }

  private void updateMetaDataSection(DebugSnapshot snapshot) {
    Map<String, String> props = new LinkedHashMap<>();
    if (snapshot.margin() != null) {
      props.put("margin", snapshot.margin().serialize());
    }
    if (snapshot.border() != null) {
      props.put("border", snapshot.border().serialize());
    }
    if (snapshot.padding() != null) {
      props.put("padding", snapshot.padding().serialize());
    }
    metadataSection.updateContent(props);
  }

  private void updateComputedStylesSection(PropertyContainer computedStyles) {
    Map<String, String> props = new LinkedHashMap<>();
    for (CSSProperty property : CSSProperty.values()) {
      if (computedStyles.wasSet(property)) {
        props.put(property.serialize(), computedStyles.get(property).serialize() + ';');
      }
    }
    computedSection.updateContent(props);
  }

  private void addOrUpdateStyleRuleSection(WeightedStyleRule weightedRule, Set<String> activeRuleKeys) {
    String selectorTitle = String.valueOf(weightedRule.rule().complexSelectors());
    String ruleKey = "rule_" + weightedRule.hashCode() + "_" + selectorTitle;
    activeRuleKeys.add(ruleKey);

    Map<String, String> declarations = new LinkedHashMap<>();
    for (Declaration decl : weightedRule.rule().declarations()) {
      declarations.put(decl.name(), decl.evaluate().serialize() + ';');
    }

    SwingStyleSegment section = ruleSections.computeIfAbsent(
      ruleKey, _1 -> {
        SwingStyleSegment newSection = new SwingStyleSegment(
          "Rule: " + selectorTitle, false);
        JSeparator divider = createDivider();
        SegmentAndDivider segmentAndDivider = new SegmentAndDivider(newSection, divider);
        mainContainer.add(newSection);
        mainContainer.add(divider);

        return segmentAndDivider;
      }).segment();
    
    section.setTitle("Rule: " + selectorTitle);
    section.updateContent(declarations);
  }

  private void removeObsoleteEntries(Set<String> activeRuleKeys) {
    Iterator<Map.Entry<String, SegmentAndDivider>> it = ruleSections.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, SegmentAndDivider> entry = it.next();
      if (!activeRuleKeys.contains(entry.getKey())) {
        mainContainer.remove(entry.getValue().segment());
        mainContainer.remove(entry.getValue().divider());
        it.remove();
      }
    }
  }

  private JSeparator createDivider() {
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    separator.setForeground(UIManager.getColor("Separator.foreground"));
    
    return separator;
  }
  
  private static record SegmentAndDivider(
    SwingStyleSegment segment,
    JSeparator divider
  ) {}

}