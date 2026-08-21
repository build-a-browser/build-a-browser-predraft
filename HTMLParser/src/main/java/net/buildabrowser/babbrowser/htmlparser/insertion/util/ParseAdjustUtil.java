package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseAdjustUtil {
  
  private static final Set<String> IMPLIED_END_TAGS = Set.of(
    "dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc");
  private static final Set<String> IMPLIED_END_TAGS_THOROUGH = Set.of(
    "caption", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr",
    "dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc");

  // TODO: Use qualified names, support MATHML and SVG namespace
  private static final Set<String> DEFAULT_SCOPE = Set.of(
    "applet", "caption", "html", "table", "td", "th", "marquee", "object",
    "select", "template");

  private static final Set<String> LI_SCOPE = mergeSet(DEFAULT_SCOPE, Set.of("ol", "ul"));
  private static final Set<String> BUTTON_SCOPE = mergeSet(DEFAULT_SCOPE, Set.of("button"));
  private static final Set<String> TABLE_SCOPE = Set.of("html", "table", "template");

  private ParseAdjustUtil() {}

  public static void generateImpliedEndTags(OpenElementStack stack) {
    while (
      stack.peek() instanceof Element element
      && IMPLIED_END_TAGS.contains(element.name())
    ) {
      stack.popNode();
    }
  }

  public static void generateAllImpliedEndTagsThoroughly(
    OpenElementStack stack
  ) {
    while (
      stack.peek() instanceof Element element
      && IMPLIED_END_TAGS_THOROUGH.contains(element.name())
    ) {
      stack.popNode();
    }
  }

  public static void generateImpliedEndTags(
    OpenElementStack stack, Set<String> exceptions
  ) {
    while (
      stack.peek() instanceof Element element
      && IMPLIED_END_TAGS.contains(element.name())
      && !exceptions.contains(element.name())
    ) {
      stack.popNode();
    }
  }

  public static void closeAPElement(ParseContext parseContext) {
    OpenElementStack stack = parseContext.openElementStack();
    generateImpliedEndTags(stack, Set.of("p"));
    if (!(stack.
      peek() instanceof Element element
      && element.name().equals("p")
    )) {
      parseContext.parseError();
    }

    Iterator<Node> stackIt = stack.iterator();
    while (stackIt.hasNext()) {
      Node node = stackIt.next();
      stackIt.remove();
      if (
        node instanceof Element element
        && element.name().equals("p")
      ) return;
    }
  }

  // TODO: Use qualified names
  private static boolean hasInScopeRaw(
    OpenElementStack openElementStack, Set<String> elementTypes, String elementName
  ) {
    Iterator<Node> stackIt = openElementStack.iterator();
    while (stackIt.hasNext()) {
      Node node = stackIt.next();
      if (node instanceof Element element) {
        if (element.name().equals(elementName)) {
          return true;
        } else if (elementTypes.contains(element.name())) {
          return false;
        }
      }
    }

    assert false;
    return false;
  }

  public static boolean hasInScope(OpenElementStack openElementStack, String elementName) {
    return hasInScopeRaw(openElementStack, DEFAULT_SCOPE, elementName);
  }

  public static boolean hasInListItemScope(OpenElementStack openElementStack, String elementName) {
    return hasInScopeRaw(openElementStack, LI_SCOPE, elementName);
  }

  public static boolean hasInButtonScope(OpenElementStack openElementStack, String elementName) {
    return hasInScopeRaw(openElementStack, BUTTON_SCOPE, elementName);
  }

  public static boolean hasInTableScope(OpenElementStack openElementStack, String elementName) {
    return hasInScopeRaw(openElementStack, TABLE_SCOPE, elementName);
  }

  public static void popUntil(OpenElementStack openElementStack, String elementName) {
    while (!(
      openElementStack.popNode() instanceof HTMLElement element
      && element.name().equals(elementName)
    ));
  }

  private static <T> Set<T> mergeSet(Set<T> set1, Set<T> set2) {
    Set<T> set3 = new HashSet<>();
    set3.addAll(set1);
    set3.addAll(set2);
    return Set.copyOf(set3);
  }

}
