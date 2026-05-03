package net.buildabrowser.babbrowser.common.util;

import java.util.HashMap;
import java.util.Map;

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

  @SuppressWarnings("unchecked")
  public static <T, U> Map<T, U> mapOf(Object... values) {
    Map<T, U> map = new HashMap<>();
    for (int i = 0; i < values.length; i += 2) {
      map.put((T) values[i], (U) values[i + 1]);
    }

    return Map.copyOf(map);
  }

  public static interface ThrowingSupplier<T> {
  
    T get() throws Exception;
    
  }

  public static interface ThrowingSupplierVoid {
  
    void get() throws Exception;
    
  }

}
