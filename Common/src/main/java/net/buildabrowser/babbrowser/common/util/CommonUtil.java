package net.buildabrowser.babbrowser.common.util;

public final class CommonUtil {
  
  private CommonUtil() {}

  public static <T> T rethrow(ThrowingSupplier<T> func) {
    try {
      return func.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void rethrowV(ThrowingSupplierVoid func) {
    try {
      func.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Avoid Optional
  public static <T> T tryOrNull(ThrowingSupplier<T> func) {
    try {
      return func.get();
    } catch (Exception e) {
      return null;
    }
  }

  public static interface ThrowingSupplier<T> {
  
    T get() throws Exception;
    
  }

  public static interface ThrowingSupplierVoid {
  
    void get() throws Exception;
    
  }

}
