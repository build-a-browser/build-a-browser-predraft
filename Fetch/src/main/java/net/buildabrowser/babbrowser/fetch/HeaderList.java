package net.buildabrowser.babbrowser.fetch;

import java.util.ArrayList;
import java.util.List;

public record HeaderList(List<Header> headers) {
  
  public static record Header(String name, byte[] value) {}

  public static HeaderList create(String... values) {
    List<Header> headers = new ArrayList<>();
    for (int i = 0; i < values.length; i += 2) {
      headers.add(new Header(values[i], values[i + 1].getBytes()));
    }

    return new HeaderList(headers);
  }

}
