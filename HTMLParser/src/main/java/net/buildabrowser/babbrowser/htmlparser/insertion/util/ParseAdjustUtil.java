package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.htmlparser.insertion.OpenElementStack;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public final class ParseAdjustUtil {
  
  private static final Set<String> IMPLIED_END_TAGS = Set.of(
    "dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc");

  // TODO: Use qualified names, support MATHML and SVG namespace
  private static final Set<String> DEFAULT_SCOPE = Set.of(
    "applet", "caption", "html", "table", "td", "th", "marquee", "object",
    "select", "template");

  private static final Set<String> BUTTON_SCOPE = mergeSet(DEFAULT_SCOPE, Set.of("button"));

  private ParseAdjustUtil() {}

  public static void generateImpliedEndTags(OpenElementStack stack) {
    while (
      stack.peek() instanceof Element element
      && IMPLIED_END_TAGS.contains(element.name())
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
  public static boolean hasInScope(
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

  public static boolean hasInButtonScope(OpenElementStack openElementStack, String elementName) {
    return hasInScope(openElementStack, BUTTON_SCOPE, elementName);
  }

  private static <T> Set<T> mergeSet(Set<T> set1, Set<T> set2) {
    Set<T> set3 = new HashSet<>();
    set3.addAll(set1);
    set3.addAll(set2);
    return Set.copyOf(set3);
  }

}
