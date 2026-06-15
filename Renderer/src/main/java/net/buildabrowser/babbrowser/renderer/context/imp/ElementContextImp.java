package net.buildabrowser.babbrowser.renderer.context.imp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesGenerator;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint.PresentationalHintName;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHintResolver;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public class ElementContextImp implements ElementContext, PropertyContainer {

  private static final SelectorSpecificity ATTR_SPECIFICITY =
    new SelectorSpecificity(true, 0, 0, 0);

  // TreeSet has a ton of overhead, sort on access instead
  private final List<WeightedStyleRule> styleRules = new ArrayList<>(1);
  private final HTMLElement element;
  private final short slotFamilyId;
  // TODO: Remove the need for this field

  private InvalidationLevel invalidationLevel = InvalidationLevel.NONE;
  private ActiveStyles activeStyles;
  private WeightedStyleRule internalStyleRule;
  private PresentationalHint legacyAttributes;
  // ELEMENT is not stored in targetedProperties because it is common, so we avoid the wrapper tax
  private TargetedPropertiesHolder targetedProperties;
  private ElementBox box;
  private ElementContext next;

  public ElementContextImp(HTMLElement element, short slotFamily) {
    this.element = element;
    this.slotFamilyId = slotFamily;
    updateStyle(element.getAttribute("style"));
    // TODO: More efficient iterator (but it does not exist yet)
    for (String attribute: element.getAttributeNames()) {
      onAttributeValueChanged(attribute, null, element.getAttribute(attribute));
    }
  }
  
  @Override
  public void onCSSRuleMatched(WeightedStyleRule styleRule) {
    SelectorTarget target = styleRule.target();
    if (target.equals(SelectorTarget.ELEMENT)) {
      styleRules.add(styleRule);
    } else {
      TargetedPropertiesHolder holder = IntrusiveList.find(
        targetedProperties, h -> h.target().equals(target));
      if (holder == null) {
        holder = new TargetedPropertiesHolder(target);
        targetedProperties = IntrusiveList.insert(targetedProperties, 0, holder);
      }
      holder.matchRule(styleRule);
    }
  }

  @Override
  public void onCSSRuleUnmatched(WeightedStyleRule styleRule) {
    SelectorTarget target = styleRule.target();
    if (target.equals(SelectorTarget.ELEMENT)) {
      styleRules.remove(styleRule);
    } else {
      TargetedPropertiesHolder holder = IntrusiveList.find(
        targetedProperties, h -> h.target().equals(target));
      assert holder != null; // Calls should be balanced
      if (holder != null) {
        holder.unmatchRule(styleRule);
      }
    }
  }

  // TODO: Use a qualified name instead
  @Override
  public void onAttributeValueChanged(String attrName, String oldValue, String newValue) {
    if (attrName.equals("style")) {
      updateStyle(newValue);
    }

    updateLegacyAttrs(attrName, newValue);
  }

  @Override
  public void regenerateStyles(StyleCache styleCache) {
    ActiveStyles oldStyles = this.activeStyles;
    PropertyContainer parentProperties = parent();
    styleRules.sort(WeightedStyleRule::compare);
    this.activeStyles = styleCache.lookupOrElse(styleRules,
      rules -> ActiveStylesGenerator.generateActiveStyles(styleRules, parentProperties));

    invalidateIfChangedStyles(oldStyles, parentProperties);

    regenerateTargetedStyles(styleCache);
  }

  @Override
  public PropertyContainer properties() {
    assert this.activeStyles != null;
    return this;
  }
  
  @Override
  public PropertyContainer targetedProperties(SelectorTarget target) {
    assert this.activeStyles != null;
    if (target.equals(SelectorTarget.ELEMENT)) {
      return this;
    }

    TargetedPropertiesHolder holder = IntrusiveList.find(
      targetedProperties, h -> h.target().equals(target));
    if (holder == null) return null;
    assert holder.container() != null;
    return holder.container();
  }

  @Override
  public HTMLElement element() {
    return this.element;
  }

  @Override
  public void setBox(ElementBox box) {
    this.box = box;
  }

  @Override
  public ElementBox box() {
    return this.box;
  }

  private void updateLegacyAttrs(String attrName, String newValue) {
    PresentationalHintName legacyAttrName = PresentationalHint.lookup(attrName);
    if (legacyAttrName == null) return;
    removeLegacyAttribute(legacyAttrName);

    PresentationalHint newAttribute = PresentationalHintResolver.resolvePresentationalHint(
      element.name(), legacyAttrName, newValue);
    if (newAttribute == null) return;
    onCSSRuleMatched(newAttribute.rule());
    newAttribute.setNext(legacyAttributes);
    this.legacyAttributes = newAttribute;
  }

  private void removeLegacyAttribute(PresentationalHintName legacyAttrName) {
    if (legacyAttributes == null) return;
    if (legacyAttributes.name().equals(legacyAttrName)) {
      this.legacyAttributes = legacyAttributes.next();
      return;
    }

    PresentationalHint prevAttr = legacyAttributes;
    PresentationalHint currentAttr = legacyAttributes.next();
    while (currentAttr != null) {
      if (currentAttr.name().equals(legacyAttrName)) {
        prevAttr.setNext(currentAttr.next());
        currentAttr.setNext(null);
        onCSSRuleUnmatched(currentAttr.rule());
        return;
      }

      prevAttr = prevAttr.next();
      currentAttr = currentAttr.next();
    }
  }

  private void updateStyle(String styleStr) {
    if (internalStyleRule != null) {
      onCSSRuleUnmatched(internalStyleRule);
      internalStyleRule = null;
    }

    if (styleStr != null) {
      // TODO: Might be good to find a better way to pass the CSS parser
      // For now, it is cached as a singleton
      // Also, how will !important factor into the below??
      Document nodeDocument = element.nodeDocument();
      URI baseURL = nodeDocument instanceof HTMLDocument htmlDocument ? htmlDocument.baseURL() : nodeDocument.url();
      CSSTokenStreamSource source = new CSSTokenStreamSource(baseURL);
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(styleStr);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(source, tokenizerInput);
      List<Declaration> declarations = CommonUtil.rethrow(
        () -> CSSParser.create().parseAStyleBlocksContents(tokenizerStream));
      // Need to do some dumb constructors to convert it to a WeightedStyleRule, maybe improve this later...
      StyleRule styleRule = new StyleRule(List.of(), declarations);
      // also why wasn't there a .create anyways?
      WeightedStyleRule weightedStyleRule = WeightedStyleRule.create(
        styleRule, ATTR_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
      onCSSRuleMatched(weightedStyleRule);
    }
  }

  private void regenerateTargetedStyles(StyleCache styleCache) {
    TargetedPropertiesHolder prev = null;
    TargetedPropertiesHolder currentHolder = targetedProperties;
    while (currentHolder != null) {
      TargetedPropertiesHolder next = currentHolder.next();

      List<WeightedStyleRule> currentRules = currentHolder.matchedRules();
      regenerateTargetedStyles(styleCache, currentRules, currentHolder);

      boolean removeCurrent = currentRules.isEmpty();
      if (removeCurrent) {
        if (prev == null) {
          targetedProperties = currentHolder.next();
        } else {
          prev.setNext(currentHolder.next());
        }
      } else {
        prev = prev == null ? null : currentHolder;
      }
      currentHolder = next;
    }
  }

  private void regenerateTargetedStyles(
    StyleCache styleCache,
    List<WeightedStyleRule> rulesList,
    TargetedPropertiesHolder holder
  ) {
    PropertyContainer oldProperties = holder.container();
    ActiveStyles targetedStyles = styleCache.lookupOrElse(rulesList,
      rules -> ActiveStylesGenerator.generateActiveStyles(rules, this));
    PropertyContainer newProperties = ActiveStyles.parentStyles(this, targetedStyles);
    holder.setContainer(newProperties);

    invalidateIfChangedProperties(oldProperties, newProperties);
  }

  private void invalidateIfChangedStyles(ActiveStyles oldStyles, PropertyContainer parentProperties) {
    if (oldStyles == null) {
      invalidate(InvalidationLevel.BOX);
    } else {
      // TODO: This is an inefficient way to do this, but we can't put a change listener on the
      //   ActiveStyles since it is regenerated from scratch (to make sure selector specificity,
      //   vars, etc. are respected each render)
      for (CSSProperty property : CSSProperty.values()) {
        if (property.hasExpansion()) continue;
        CSSValue newValue = activeStyles.getProperty(parentProperties, property);
        CSSValue oldValue = oldStyles.getProperty(parentProperties, property);
        if (!newValue.equals(oldValue)) {
          invalidate(property.invalidationLevel());
        }
      }
    }
  }

  private void invalidateIfChangedProperties(PropertyContainer oldProperties, PropertyContainer newProperties) {
    if (oldProperties == null) {
      invalidate(InvalidationLevel.BOX);
    } else {
      // TODO: Same as above
      for (CSSProperty property : CSSProperty.values()) {
        if (property.hasExpansion()) continue;
        CSSValue newValue = newProperties.get(property);
        CSSValue oldValue = oldProperties.get(property);
        if (!newValue.equals(oldValue)) {
          invalidate(property.invalidationLevel());
        }
      }
    }
  }

  // Directly implement PropertyContainer to save some allocations

  @Override
  public PropertyContainer parent() {
    return
      element.parentNode() instanceof HTMLElement parent
      && SlotItem.getExistingById(parent, slotFamilyId) instanceof ElementContext parentContext ?
      parentContext.properties() : null;
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return parent() != null && activeStyles.shouldInherit(property);
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return activeStyles.getProperty(parent(), property);
  }

  @Override
  public CSSValue getCustom(String property) {
    return activeStyles.getCustom(property);
  }

  // Slottable

  @Override
  public short familyId() {
    return this.slotFamilyId;
  }

  @Override
  public ElementContext next() {
    return this.next;
  }

  @Override
  public void setNext(ElementContext next) {
    this.next = next;
  }

  // Invalidatable

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    if (invalidationLevel.ordinal() < this.invalidationLevel.ordinal()) {
      this.invalidationLevel = invalidationLevel;
      if (element.parentNode() instanceof HTMLElement htmlElement) {
        ElementContext context = SlotItem.getExistingById(htmlElement, slotFamilyId);
        context.invalidate(invalidationLevel);
      } else if (
        element.parentNode() instanceof HTMLDocument document
        && document.renderer() != null
      ) {
        document.renderer().onDocumentInvalidated(invalidationLevel);
      }
    }
  }

  @Override
  public void validate() {
    if (this.invalidationLevel == InvalidationLevel.NONE) return;
    
    this.invalidationLevel = InvalidationLevel.NONE;
    Node currentNode = element.firstChild();
    while (currentNode != null) {
      if (currentNode instanceof HTMLElement htmlElement) {
        ElementContext context = SlotItem.getExistingById(htmlElement, slotFamilyId);
        context.validate();
      }
      currentNode = currentNode.nextSibling();
    }
  }

  @Override
  public InvalidationLevel invalidationLevel() {
    return this.invalidationLevel;
  }
  
}
