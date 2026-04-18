package net.buildabrowser.babbrowser.fetch.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.fetch.HeaderList;

public class HeaderListImp implements HeaderList {
  
  // I think this should generally be small enough to not need a map
  private final List<Header> headers;

  public HeaderListImp() {
    this.headers = new ArrayList<>();
  }

  @Override
  public void append(String name, String value) {
    for (Header header: headers) {
      if (header.name().equalsIgnoreCase(name)) {
        IntrusiveList.add(header, new HeaderImp(header.name(), value));
        return;
      }
    }

    headers.add(new HeaderImp(name, value));
  }

  @Override
  public String get(String name) {
    for (Header header: headers) {
      if (header.name().equalsIgnoreCase(name)) {
        return header.value();
      }
    }

    return null;
  }

  @Override
  public List<String> extractHeaderListValues(String name) {
    // TODO: Properly implement the spec
    String value = get(name);
    if (value == null) return List.of();
    return List.of(value.split(",")).stream().map(s -> s.strip()).toList();
  }

}
