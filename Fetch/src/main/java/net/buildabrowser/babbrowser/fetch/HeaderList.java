package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.fetch.imp.HeaderListImp;

public interface HeaderList {

  void append(String name, String value);

  String get(String name);
  
  static interface Header extends IntrusiveList<Header> {

    String name();

    String value();

  }

  public static HeaderList create() {
    return new HeaderListImp();
  }

  public static HeaderList create(String... values) {
    HeaderList headers = new HeaderListImp();
    for (int i = 0; i < values.length; i += 2) {
      headers.append(values[i], values[i + 1]);
    }

    return headers;
  }

}
