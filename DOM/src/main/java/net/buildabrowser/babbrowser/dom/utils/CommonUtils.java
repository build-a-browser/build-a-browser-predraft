package net.buildabrowser.babbrowser.dom.utils;

public final class CommonUtils {
  
  private CommonUtils() {}

  public static <T> T rethrow(ThrowingSupplier<T> func) {
    try {
      return func.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static interface ThrowingSupplier<T> {
  
    T get() throws Exception;
    
  }

}
