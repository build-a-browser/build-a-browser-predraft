package net.buildabrowser.babbrowser.cookies.exception;

import java.io.IOException;

public class CookieStoreException extends IOException {

  public CookieStoreException() {}

  public CookieStoreException(String message) {
    super(message);
  }

  public CookieStoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public CookieStoreException(Throwable cause) {
    super(cause);
  }
  
}
