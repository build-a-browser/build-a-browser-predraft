package net.buildabrowser.babbrowser.browser.render.content.flow.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;

public final class FlowTestUtil {
  
  private FlowTestUtil() {}

  public static void assertFragmentEquals(LayoutFragment expected, LayoutFragment actual) {
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

  public static void assertFragmentListEquals(List<LayoutFragment> expected, List<LayoutFragment> actual) {
    Assertions.assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertFragmentEquals(expected.get(i), actual.get(i));
    }
  }

  private static void assertFragmentEquals(ManagedBoxFragment expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(ManagedBoxFragment.class, actual);
    ManagedBoxFragment actualFragment = (ManagedBoxFragment) actual;
    Assertions.assertEquals(expected.box(), actualFragment.box());
    Assertions.assertEquals(SinglyLinkedList._testingOnlySize(expected.fragments()), SinglyLinkedList._testingOnlySize(actualFragment.fragments()));

    SinglyLinkedList<LayoutFragment> curExpected = expected.fragments();
    SinglyLinkedList<LayoutFragment> curActual = actualFragment.fragments();
    while (curExpected != null) {
      assertFragmentEquals(curExpected.item(), curActual.item());
      curExpected = curExpected.next();
      curActual = curActual.next();
    }
  }

  private static void assertFragmentEquals(UnmanagedBoxFragment expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(UnmanagedBoxFragment.class, actual);
    UnmanagedBoxFragment actualFragment = (UnmanagedBoxFragment) actual;
    Assertions.assertEquals(expected.contentWidth(), actualFragment.contentWidth());
    Assertions.assertEquals(expected.contentHeight(), actualFragment.contentHeight());
    Assertions.assertEquals(expected.box(), actualFragment.box());
  }

  private static void assertFragmentEquals(TextFragment expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(TextFragment.class, actual);
    TextFragment actualFragment = (TextFragment) actual;
    Assertions.assertEquals(expected.text(), actualFragment.text());
  }

  public static void assertFragmentEquals(LineBoxFragment expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(LineBoxFragment.class, actual);
    LineBoxFragment actualFragment = (LineBoxFragment) actual;

    SinglyLinkedList<LayoutFragment> curExpected = expected.fragments();
    SinglyLinkedList<LayoutFragment> curActual = actualFragment.fragments();
    while (curExpected != null) {
      assertFragmentEquals(curExpected.item(), curActual.item());
      curExpected = curExpected.next();
      curActual = curActual.next();
    }
  }

}
