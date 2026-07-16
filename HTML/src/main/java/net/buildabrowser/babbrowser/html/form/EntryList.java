package net.buildabrowser.babbrowser.html.form;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.form.XWWWFormURLEncodedSerializer.NameValuePair;
import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.HTMLFormElement;

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
    List<FormAssociatedElement> controls = form.submittableElements();
    List<EntryListEntry> entryList = new ArrayList<>();
    // TODO: Preserve tree order
    for (FormAssociatedElement field: controls) {
      // TODO: All the other cases
      String name = field.getAttribute("name");
      if (name == null || name.length() == 0) continue;
      entryList.add(EntryListEntry.createFromString(name, field.value()));
    }

    // TODO: Fire formdata event
    form.setConstructingEntryList(false);;
    return new EntryList(List.copyOf(entryList));
  }

  private String normalizeNewlines(String value) {
    return value.replaceAll("\\r(?!\\n)|(?<!\\r)\\n", "\r\n");
  }

}
