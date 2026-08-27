package net.buildabrowser.babbrowser.renderer.context.imp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.context.TargetedPropertiesHolder;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint.PresentationalHintName;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHintResolver;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public class ElementContextImp extends RenderContextImp implements ElementContext {

  private static final SelectorSpecificity ATTR_SPECIFICITY =
    new SelectorSpecificity(true, 0, 0, 0);

  // TreeSet has a ton of overhead, sort on access instead
  private final List<WeightedStyleRule> styleRules = new ArrayList<>(1);
  private final HTMLElement element;
  // TODO: Remove the need for this field

  private WeightedStyleRule internalStyleRule;
  private PresentationalHint legacyAttributes;
  private ElementBox box;

  // ELEMENT is not stored in targetedProperties because it is common, so we avoid the wrapper tax
  protected TargetedPropertiesHolder targetedProperties;

  public ElementContextImp(HTMLElement element, short slotFamily) {
    super(slotFamily);
    this.element = element;
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

    invalidate(InvalidationLevel.STYLE);
    super.invalidate(InvalidationLevel.STYLE_SELF);
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

    invalidate(InvalidationLevel.STYLE);
    super.invalidate(InvalidationLevel.STYLE_SELF);
  }

  @Override
  public List<WeightedStyleRule> matchedRules() {
    styleRules.sort(WeightedStyleRule::compare);
    return styleRules;
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
  public ActiveStyles regenerateStyles(StyleCache styleCache, ActiveStyles refStyles) {
    PropertyContainer oldStyles = this.computedStyles;
    PropertyContainer parentProperties = parent();
    styleRules.sort(WeightedStyleRule::compare);
    boolean reuseLast = refStyles != null && Objects.equals(refStyles.refRules(), styleRules);
    ActiveStyles activeStyles = reuseLast ?
      refStyles :
      styleCache.lookupOrElse(styleRules,
        rules -> ActiveStylesGenerator.generateActiveStyles(styleRules));
    
    this.computedStyles = activeStyles.flatten(
      parentProperties, styleCache::cacheFlattened);
    invalidate(changedPropertyInvalidationLevel(oldStyles, this.computedStyles));

    regenerateTargetedStyles(styleCache);

    return activeStyles;
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
  
  @Override
  public TargetedPropertiesHolder targetedPropertiesHolder(SelectorTarget target) {
    assert this.computedStyles != null;
    if (target.equals(SelectorTarget.ELEMENT)) {
      throw new IllegalArgumentException("Cannot get holder for ELEMENT");
    }

    TargetedPropertiesHolder holder = IntrusiveList.find(
      targetedProperties, h -> h.target().equals(target));
    if (holder == null) return null;
    return holder;
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
      Document nodeDocument = element.nodeDocument();
      URI baseURL = nodeDocument instanceof HTMLDocument htmlDocument ? htmlDocument.baseURL() : nodeDocument.url();
      CSSTokenStreamSource source = new CSSTokenStreamSource(baseURL);
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(styleStr);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(source, tokenizerInput);
      List<Declaration> declarations = CommonUtil.rethrow(
        () -> CSSParser.create().parseAStyleBlocksContents(tokenizerStream));
      // Need to do some dumb constructors to convert it to a WeightedStyleRule, maybe improve this later...
      StyleRule styleRule = new StyleRule(List.of(), declarations);
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
      rules -> ActiveStylesGenerator.generateActiveStyles(rules));
    
    PropertyContainer newProperties = isBeforeOrAfterWithoutContent(holder) ?
      null :
      targetedStyles.flatten(
        computedStyles, styleCache::cacheFlattened);
    holder.setContainer(newProperties);

    invalidateTargetIfChanged(holder, oldProperties, newProperties);
  }

  private void invalidateTargetIfChanged(
    TargetedPropertiesHolder holder,
    PropertyContainer oldProperties,
    PropertyContainer newProperties
  ) {
    short invalidationLevel = changedPropertyInvalidationLevel(oldProperties, newProperties);
    ElementBox relatedBox = holder.relatedContext() == null ? null :
      holder.relatedContext().box();
    if (
      relatedBox == null
      && box != null
      && (holder.target().equals(SelectorTarget.BEFORE)
      || holder.target().equals(SelectorTarget.AFTER))
    ) {
      if ((invalidationLevel & InvalidationLevel.BOX) != 0) {
        invalidate(InvalidationLevel.BOX);
      }
      return;
    } else if (
      relatedBox == null
      && box != null
    ) {
      invalidate(invalidationLevel);
    } else if (relatedBox != null) {
      relatedBox.context().invalidate(invalidationLevel);
    }
  }

  private PropertyContainer parent() {
    return
      element.parentNode() instanceof HTMLElement parent
      && SlotItem.getExistingById(parent, familyId()) instanceof ElementContext parentContext ?
      parentContext.properties() : null;
  }

  // Invalidatable

  @Override
  public void invalidate(short invalidationLevel) {
    if ((invalidationLevel & invalidationLevel()) != invalidationLevel) {
      super.invalidate(invalidationLevel);

      invalidationLevel &= ~InvalidationLevel.STYLE_SELF;
      
      if (element.parentNode() instanceof HTMLElement htmlElement) {
        RenderContext parentContext = SlotItem.getExistingById(htmlElement, familyId());

        if (
          (invalidationLevel & InvalidationLevel.LAYOUT) != 0
          && box != null
          && !PositionUtil.affectsLayoutInvalidation(box)
        ) {
          super.invalidate(invalidationLevel); // To prevent a loop
          box.context().invalidate(invalidationLevel);
          if (element.nodeDocument() instanceof HTMLDocument document) {
            document.renderer().onDocumentInvalidated(invalidationLevel);
          }
        } else if (parentContext != null) {
          parentContext.invalidate(invalidationLevel);
        }
      } else if (
        box != null
        && box.parentBox() instanceof ElementBox elementBox
        && elementBox.context() != null
      ) {
        elementBox.context().invalidate(invalidationLevel);
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
    if (invalidationLevel() == InvalidationLevel.NONE) return;
    
    Node currentNode = element.firstChild();
    while (currentNode != null) {
      if (currentNode instanceof HTMLElement htmlElement) {
        RenderContext context = SlotItem.getExistingById(htmlElement, familyId());
        context.validate();
      }
      currentNode = currentNode.nextSibling();
    }

    super.validate();
  }

  private boolean isBeforeOrAfterWithoutContent(TargetedPropertiesHolder holder) {
    if (
      !SelectorTarget.BEFORE.equals(holder.target())
      && !SelectorTarget.AFTER.equals(holder.target())
    ) return false;

    for (WeightedStyleRule rule: holder.matchedRules()) {
      for (Declaration declaration: rule.rule().declarations()) {
        if (declaration.name().equalsIgnoreCase("content")) {
          return false;
        }
      }
    }

    return true;
  }
  
}
