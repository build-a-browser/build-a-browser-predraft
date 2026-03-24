package net.buildabrowser.babbrowser.common.util;

public final class CommonUtil {
  
  private CommonUtil() {}

  public static <T> T rethrow(Rethrowable<T> func) {
    try {
      return func.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static interface Rethrowable<T> {
  
    T get() throws Exception;
    
  }

}
