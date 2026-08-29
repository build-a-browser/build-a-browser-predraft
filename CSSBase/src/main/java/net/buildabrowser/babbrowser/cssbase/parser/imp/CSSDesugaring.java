package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector.LogicalPseudoSelectorType;
import net.buildabrowser.babbrowser.cssbase.selector.NestingSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorReferencingSelectorPart;

public final class CSSDesugaring {
  
  private CSSDesugaring() {}

  public static List<ComplexSelector> desugarSelectors(
    List<ComplexSelector> parentSelectors,
    List<ComplexSelector> sugarSelectors,
    boolean isContinuation
  ) {
    if (isContinuation) {
      return sugarSelectors;
    }
    if (parentSelectors.isEmpty()) {
      for (ComplexSelector selector: sugarSelectors) {
        if (hasNestedSelector(selector)) {
          return desugarTopLevelSelectors(sugarSelectors);
        }
      }
      return sugarSelectors;
    }

    List<SelectorPart> usedParent = parentSelectors.size() == 1 ?
      parentSelectors.get(0).parts() :
      List.of(new LogicalPseudoSelector(LogicalPseudoSelectorType.IS, parentSelectors));

    List<ComplexSelector> rewrittenSelectors = new ArrayList<>(sugarSelectors.size());
    for (ComplexSelector sugarSelector: sugarSelectors) {
      ComplexSelector rewrittenSelector = hasNestedSelector(sugarSelector) ?
        rewriteNestedSelector(usedParent, sugarSelector, true) :
        rewriteRelativeSelector(usedParent, sugarSelector);
      rewrittenSelectors.add(rewrittenSelector);
    }

    return rewrittenSelectors;
  }

  private static List<ComplexSelector> desugarTopLevelSelectors(
    List<ComplexSelector> sugarSelectors
  ) {
    List<ComplexSelector> rewrittenSelectors = new ArrayList<>(sugarSelectors.size());
    for (ComplexSelector sugarSelector: sugarSelectors) {
      ComplexSelector rewrittenSelector = hasNestedSelector(sugarSelector) ?
        rewriteNestedSelector(List.of(), sugarSelector, true) :
        sugarSelector;
      rewrittenSelectors.add(rewrittenSelector);
    }

    return rewrittenSelectors;
  }

  private static ComplexSelector rewriteRelativeSelector(
    List<SelectorPart> usedParent, ComplexSelector sugarSelector
  ) {
    List<SelectorPart> oldParts = sugarSelector.parts();
    List<SelectorPart> rewrittenParts = new ArrayList<>(oldParts.size() + usedParent.size());
    rewrittenParts.addAll(usedParent);
    rewrittenParts.addAll(oldParts);
    return new ComplexSelector(rewrittenParts);
  }

  private static boolean hasNestedSelector(ComplexSelector sugarSelector) {
    for (SelectorPart part: sugarSelector.parts()) {
      if (part instanceof NestingSelector) {
        return true;
      } else if (part instanceof SelectorReferencingSelectorPart selRefSelector) {
        for (ComplexSelector subselector: selRefSelector.complexSelectors()) {
          if (hasNestedSelector(subselector)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  private static ComplexSelector rewriteNestedSelector(
    List<SelectorPart> usedParent,
    ComplexSelector sugarSelector,
    boolean isRoot
  ) {
    List<SelectorPart> oldParts = sugarSelector.parts();
    List<SelectorPart> rewrittenParts = new ArrayList<>(oldParts.size());
    boolean skipDescendant = isRoot;
    for (SelectorPart oldPart: oldParts) {
      if (skipDescendant) {
        if (oldPart instanceof DescendantCombinator) continue;
        if (oldPart instanceof Combinator) {
          rewrittenParts.addAll(usedParent);
        }
        skipDescendant = false;
      }

      if (oldPart instanceof NestingSelector) {
        rewrittenParts.addAll(usedParent);
      } else if (oldPart instanceof SelectorReferencingSelectorPart selRefSelector) {
        List<ComplexSelector> oldChildren = selRefSelector.complexSelectors();
        List<ComplexSelector> newChildren = new ArrayList<>(oldChildren.size());
        for (ComplexSelector oldChild: oldChildren) {
          newChildren.add(rewriteNestedSelector(usedParent, oldChild, false));
        }
        SelectorPart rewrittenSelector = selRefSelector.rewrite(newChildren);
        rewrittenParts.add(rewrittenSelector);
      } else {
        rewrittenParts.add(oldPart);
      }
    }

    return new ComplexSelector(rewrittenParts);
  }

}
