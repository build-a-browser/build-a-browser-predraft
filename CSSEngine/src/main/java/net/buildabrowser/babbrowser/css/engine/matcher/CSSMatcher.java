package net.buildabrowser.babbrowser.css.engine.matcher;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.imp.CSSMatcherImp;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface CSSMatcher {
  
  DocumentChangeListener documentChangeListener();

  void applyStylesheets(Document document, MediaContext mediaContext);

  ElementRootSet allElements();

  boolean changed();

  interface CSSMatcherContext {
    
    default void onMatched(Node node, WeightedStyleRule matchedRule) {};

    default void onUnmatched(Node node, WeightedStyleRule matchedRule) {};

    default boolean isFocusVisible(Element element) {
      return false;
    }

  }

  static CSSMatcher create(
    CSSMatcherContext context,
    StyleSheetList uaStyleSheets,
    SlotFamilyFamily slotFamilyFamily
  ) {
    return new CSSMatcherImp(context, uaStyleSheets, slotFamilyFamily);
  }

}
