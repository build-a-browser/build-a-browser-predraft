package net.buildabrowser.babbrowser.stream;

public interface ReadableStreamController {
  
  // TODO: I don't know if this is a good place for the spec's "Internal methods"
  void cancel(Object reason);

  void pull(ReadRequest readRequest);

}
