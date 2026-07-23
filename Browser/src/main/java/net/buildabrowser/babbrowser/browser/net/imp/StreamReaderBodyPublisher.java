package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.IOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class StreamReaderBodyPublisher implements BodyPublisher {

  private final ReadableStreamDefaultReader reader;

  public StreamReaderBodyPublisher(ReadableStreamDefaultReader reader) {
    this.reader = reader;
  }

  @Override
  public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
    subscriber.onSubscribe(new Subscription() {

      @Override
      public void request(long n) {
        // TODO: Support chunk-by-chunk
        reader.readAllBytes(
          bytes -> {
            subscriber.onNext(ByteBuffer.wrap(bytes));
            subscriber.onComplete();
          },
          err -> subscriber.onError(
            err instanceof Throwable throwable ? throwable :
            err instanceof String message ? new IOException(message) :
            new IOException("Reading body completed abnormally")));
      }

      @Override
      public void cancel() {
        // TODO: Support cancellation
      }
      
    });
  }

  @Override
  public long contentLength() {
    return -1;
  }

}
