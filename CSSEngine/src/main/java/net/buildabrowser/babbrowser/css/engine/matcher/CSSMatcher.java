package net.buildabrowser.babbrowser.css.engine.matcher;

import net.buildabrowser.babbrowser.css.engine.matcher.imp.CSSMatcherImp;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;

public interface CSSMatcher {
  
  DocumentChangeListener documentChangeListener();

  void applyStylesheets(Document document);

  interface CSSMatcherContext {
    
    void onMatched(Node node, StyleRule matchedRule);

    void onUnmatched(Node node, StyleRule matchedRule);

  }

  static CSSMatcher create(CSSMatcherContext context) {
    return new CSSMatcherImp(context);
  }

}
