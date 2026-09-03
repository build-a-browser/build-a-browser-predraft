package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.IOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class StreamReaderBodyPublisher implements BodyPublisher {

  private final FetchBody body;
  private final ReadableStreamDefaultReader reader;

  public StreamReaderBodyPublisher(
    FetchBody body,
    ReadableStreamDefaultReader reader
  ) {
    this.body = body;
    this.reader = reader;
  }

  @Override
  public void subscribe(Subscriber<? super ByteBuffer> subscriber) {
    subscriber.onSubscribe(new Subscription() {

      private final AtomicBoolean started = new AtomicBoolean(false);

      @Override
      public void request(long n) {
        if (!
          started.compareAndSet(false, true)
        ) return;

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
    return body.length();
  }

}
