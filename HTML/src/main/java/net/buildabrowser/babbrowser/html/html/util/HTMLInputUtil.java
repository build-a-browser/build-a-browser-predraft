package net.buildabrowser.babbrowser.html.html.util;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.util.List;
import java.util.stream.Collectors;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.html.SubmittableElementSet;

public final class HTMLInputUtil {
  
  private HTMLInputUtil() {}

  // TODO: Need to call this in a number of other cases
  public static void deselectOtherRadioElements(
    HTMLInputElement refElement
  ) {
    HTMLFormElement formOwner = refElement.formOwner();
    String name = refElement.getAttribute("name");
    if (
      name == null
      || name.length() == 0
    ) return;
    SubmittableElementSet formItems = formOwner == null ?
      ((HTMLDocument) refElement.nodeDocument()).unownedSubmittableElements() :
      formOwner.submittableElements();
    List<HTMLInputElement> radioBoxElements = formItems.elements()
      .stream()
      .filter(e -> isHtmlElement(e, "input"))
      .map(e -> (HTMLInputElement) e)
      // TODO: Check same tree
      .filter(e ->
        e != refElement
        && "radio".equals(e.type())
        && e.hasAttribute("name")
        && e.getAttribute("name").equals(name))
      .collect(Collectors.toList());
    
    for (HTMLInputElement el: radioBoxElements) {
      el.setCheckedRaw(false);
    }
  }

}
