package net.buildabrowser.babbrowser.stream;

import java.util.function.Consumer;

public interface ReadableStreamDefaultReader extends ReadableStreamGenericReader {
  
  void readAllBytes(Consumer<byte[]> successSteps, Consumer<Object> failureSteps);

}
