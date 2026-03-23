package net.buildabrowser.babbrowser.stream.imp;

import net.buildabrowser.babbrowser.stream.ReadableStreamGenericReader;

public abstract class ReadableStreamGenericReaderImp implements ReadableStreamGenericReader {
  
  final ReadableStreamImp stream;

  public ReadableStreamGenericReaderImp(ReadableStreamImp stream) {
    this.stream = stream;
  }

}
