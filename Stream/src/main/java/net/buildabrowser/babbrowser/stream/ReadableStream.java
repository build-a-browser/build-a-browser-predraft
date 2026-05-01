package net.buildabrowser.babbrowser.stream;

import net.buildabrowser.babbrowser.stream.imp.ReadableStreamImp;

public interface ReadableStream {

  ReadableStreamGenericReader getReader(ReadableStreamGetReaderOptions options);
  
  // TODO: Also accept queuing strategy
  static ReadableStream create(UnderlyingSource underlyingSource) {
    return new ReadableStreamImp(underlyingSource);
  }

  static class ReadableStreamGetReaderOptions {
    public ReadableStreamReaderMode mode;
  }

  static enum ReadableStreamReaderMode {
    BYOB
  }

  static void enqueue(ReadableStream stream, Object chunk) {
    ((ReadableStreamImp) stream).enqueue(chunk);
  }

  static void close(ReadableStream stream) {
    ((ReadableStreamImp) stream).requestClose();
  }

}
