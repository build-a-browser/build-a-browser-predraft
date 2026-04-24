package net.buildabrowser.babbrowser.common.util;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class GCUtil {

  private static long FAST_GC_FREQUENCY = 5000;
  private static long SLOW_GC_FREQUENCY = 30000;

  private static long lastFastGCTime = System.currentTimeMillis();
  private static long lastSlowGCTime = System.currentTimeMillis();
  
  private GCUtil() {}

  public static void fastGC() {
    if (lastFastGCTime + FAST_GC_FREQUENCY > System.currentTimeMillis()) return;
    lastFastGCTime = System.currentTimeMillis();
  }

  public static void slowGC() {
    if (lastSlowGCTime + SLOW_GC_FREQUENCY > System.currentTimeMillis()) {
      fastGC();
      return;
    }

    lastSlowGCTime = System.currentTimeMillis();
    fullGC();
  }

  private static void fullGC() {
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      ObjectName name = new ObjectName("com.sun.management:type=DiagnosticCommand");
      
      server.invoke(name, "gcRun", new Object[] { new String[0] }, new String[] { String[].class.getName() });
    } catch (Exception e) {
      System.gc(); 
    }
  }

}
