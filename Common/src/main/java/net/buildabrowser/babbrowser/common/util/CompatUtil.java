package net.buildabrowser.babbrowser.common.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CompatUtil {
  
  private CompatUtil() {}

  public static <K, V> Map<K, V> mapCopyOf(Map<? extends K, ? extends V> map) {
    return Collections.unmodifiableMap(new HashMap<>(map));
  }

  public static <T> List<T> listCopyOf(Collection<? extends T> coll) {
    return Collections.unmodifiableList(new ArrayList<>(coll));
  }

  public static <T> T getFirst(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
  }

  public static <T> T getLast(List<T> list) {
    return list.isEmpty() ? null : list.get(list.size() - 1);
  }

  public static <T> T removeLast(List<T> list) {
    return list.remove(list.size() - 1);
  }

  public static float mathClamp(float value, float min, float max) {
    return Math.min(Math.max(value, min), max);
  }

  public static int mathClamp(int value, int min, int max) {
    return Math.min(Math.max(value, min), max);
  }

  public static boolean isBlank(String string) {
    return string.trim().length() == 0;
  }

  public static ByteBuffer slice(ByteBuffer buffer, int index, int length) {
    int oldPos = buffer.position();
    int oldLimit = buffer.limit();
    try {
      ((Buffer) buffer).position(index);
      ((Buffer) buffer).limit(index + length);
      
      return buffer.slice(); 
    } finally {
      ((Buffer) buffer).position(oldPos);
      ((Buffer) buffer).limit(oldLimit);
    }
  }

}
