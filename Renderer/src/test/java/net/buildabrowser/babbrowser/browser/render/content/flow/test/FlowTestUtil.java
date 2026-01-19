package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;

public final class FlowTestUtil {
  
  private FlowTestUtil() {}

  public static void assertFragmentEquals(FlowFragment expected, FlowFragment actual) {
    Assertions.assertEquals(expected.borderX(), actual.borderX());
    Assertions.assertEquals(expected.borderY(), actual.borderY());
    Assertions.assertEquals(expected.contentWidth(), actual.contentWidth());
    Assertions.assertEquals(expected.contentHeight(), actual.contentHeight());
    switch (expected) {
      case ManagedBoxFragment fragment -> assertFragmentEquals(fragment, actual);
      case UnmanagedBoxFragment fragment -> assertFragmentEquals(fragment, actual);
      case TextFragment fragment -> assertFragmentEquals(fragment, actual);
      case LineBoxFragment fragment -> assertFragmentEquals(fragment, actual);
      default -> throw new IllegalArgumentException("Unexpected fragment type!");
    }
  }

  public static void assertFragmentListEquals(List<FlowFragment> expected, List<FlowFragment> actual) {
    Assertions.assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertFragmentEquals(expected.get(i), actual.get(i));
    }
  }

  private static void assertFragmentEquals(ManagedBoxFragment expected, FlowFragment actual) {
    Assertions.assertInstanceOf(ManagedBoxFragment.class, actual);
    ManagedBoxFragment actualFragment = (ManagedBoxFragment) actual;
    Assertions.assertEquals(expected.box(), actualFragment.box());
    Assertions.assertEquals(expected.fragments().size(), actualFragment.fragments().size());
    for (int i = 0; i < expected.fragments().size(); i++) {
      assertFragmentEquals(expected.fragments().get(i), actualFragment.fragments().get(i));
    }
  }

  private static void assertFragmentEquals(UnmanagedBoxFragment expected, FlowFragment actual) {
    Assertions.assertInstanceOf(UnmanagedBoxFragment.class, actual);
    UnmanagedBoxFragment actualFragment = (UnmanagedBoxFragment) actual;
    Assertions.assertEquals(expected.contentWidth(), actualFragment.box().dimensions().getComputedWidth());
    Assertions.assertEquals(expected.contentHeight(), actualFragment.box().dimensions().getComputedHeight());
    Assertions.assertEquals(expected.box(), actualFragment.box());
  }

  private static void assertFragmentEquals(TextFragment expected, FlowFragment actual) {
    Assertions.assertInstanceOf(TextFragment.class, actual);
    TextFragment actualFragment = (TextFragment) actual;
    Assertions.assertEquals(expected.text(), actualFragment.text());
  }

  public static void assertFragmentEquals(LineBoxFragment expected, FlowFragment actual) {
    Assertions.assertInstanceOf(LineBoxFragment.class, actual);
    LineBoxFragment actualFragment = (LineBoxFragment) actual;
    Assertions.assertEquals(expected.fragments().size(), actualFragment.fragments().size());
    for (int i = 0; i < expected.fragments().size(); i++) {
      assertFragmentEquals(expected.fragments().get(i), actualFragment.fragments().get(i));
    }
  }

}
