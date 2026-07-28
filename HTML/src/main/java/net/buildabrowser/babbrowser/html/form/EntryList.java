package net.buildabrowser.babbrowser.html.form;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.form.XWWWFormURLEncodedSerializer.NameValuePair;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLButtonElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;

public record EntryList(List<EntryListEntry> entries) {
  
  // TODO: Also support File
  public static record EntryListEntry(
    String name, String value
  ) {

    public static EntryListEntry createFromString(
      String name, String value
    ) {
      // TODO: Convert to scalar
      return new EntryListEntry(name, value);
    }

  }

  public List<NameValuePair> toNameValuePairs() {
    List<NameValuePair> pairs = new ArrayList<>();
    for (EntryListEntry entry: entries) {
      String name = normalizeNewlines(entry.name());
      // TODO: Handle file value
      String value = normalizeNewlines(entry.value);
      pairs.add(new NameValuePair(name, value));
    }
    return pairs;
  }

  public static EntryList constructEntryList(
    HTMLFormElement form,
    Element submitter
    // TODO: Accept charset encoding
  ) {
    if (form.constructingEntryList()) return null;
    form.setConstructingEntryList(true);
    List<FormAssociatedElement> controls = form.submittableElements().elements();
    List<EntryListEntry> entryList = new ArrayList<>();
    // TODO: Preserve tree order
    for (FormAssociatedElement field: controls) {
      // TODO: Check datalist
      if (field.disabled()) continue;
      if (
        field != submitter
        && (
          field instanceof HTMLButtonElement
          || FormSubmissionAlgorithm.isSubmitButton(field))
      ) continue; // TODO: Correct way to check button-ness
      if (
        isCheckBoxOrRadio(field)
        && !((HTMLInputElement) field).checked()
      ) continue;

      // TODO: All the other cases
      String name = field.getAttribute("name");
      if (name == null || name.length() == 0) continue;
      if (isCheckBoxOrRadio(field)) {
        String value = field.getAttribute("value");
        entryList.add(EntryListEntry.createFromString(
          name, value == null ? "on" : value));
      } else {
        entryList.add(EntryListEntry.createFromString(name, field.value()));
      }
    }

    // TODO: Fire formdata event
    form.setConstructingEntryList(false);;
    return new EntryList(List.copyOf(entryList));
  }

  private static boolean isCheckBoxOrRadio(FormAssociatedElement field) {
    return
      isHtmlElement(field, "input")
      && field instanceof HTMLInputElement htmlInputElement
      && (
        "checkbox".equals(htmlInputElement.type())
        || "radio".equals(htmlInputElement.type()));
  }

  private String normalizeNewlines(String value) {
    return value.replaceAll("\\r(?!\\n)|(?<!\\r)\\n", "\r\n");
  }

}
