package net.buildabrowser.babbrowser.renderer.content.common.test;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.flow.FloatRefFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public final class FragmentTestUtil {
  
  private FragmentTestUtil() {}

  public static void assertFragmentEquals(LayoutFragment expected, LayoutFragment actual) {
    boolean skipCheck = switch (expected) {
      case TestFloatRefFragment fragment -> {
        assertFragmentEquals(fragment, actual);
        yield true;
      }
      default -> false;
    };
    if (skipCheck) return;

    Assertions.assertEquals(expected.posX(Measurement.BORDER), actual.posX(Measurement.BORDER));
    Assertions.assertEquals(expected.posY(Measurement.BORDER), actual.posY(Measurement.BORDER));
    Assertions.assertEquals(expected.width(Measurement.CONTENT), actual.width(Measurement.CONTENT));
    Assertions.assertEquals(expected.height(Measurement.CONTENT), actual.height(Measurement.CONTENT));
    switch (expected) {
      case ManagedBoxFragment<?> fragment -> assertFragmentEquals(fragment, actual);
      case UnmanagedBoxFragment<?> fragment -> assertFragmentEquals(fragment, actual);
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void assertFragmentListEqualsC(List<LayoutFragment> expected, List<BoxFragment<?>> actual) {
    assertFragmentListEquals(expected, (List<LayoutFragment>) (List) actual);
  }

  public static void assertFragmentListEquals(List<LayoutFragment> expected, LayoutFragment actual) {
    LayoutFragment current = actual;
    Iterator<LayoutFragment> fragIt = expected.iterator();
    while (current != null) {
      if (!fragIt.hasNext()) {
        throw new AssertionError("Fragment list sizes do not match!");
      }

      assertFragmentEquals(fragIt.next(), current);
      current = current.next();
    }
    if (fragIt.hasNext()) {
      throw new AssertionError("Fragment list sizes do not match!");
    }
  }

  private static void assertFragmentEquals(ManagedBoxFragment<?> expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(ManagedBoxFragment.class, actual);
    ManagedBoxFragment<?> actualFragment = (ManagedBoxFragment<?>) actual;
    Assertions.assertEquals(expected.box(), actualFragment.box());
    Assertions.assertEquals(
      IntrusiveList._testingOnlySize(expected.fragments()),
      IntrusiveList._testingOnlySize(actualFragment.fragments()));

    LayoutFragment curExpected = expected.fragments();
    LayoutFragment curActual = actualFragment.fragments();
    while (curExpected != null) {
      assertFragmentEquals(curExpected, curActual);
      curExpected = curExpected.next();
      curActual = curActual.next();
    }
  }

  private static void assertFragmentEquals(UnmanagedBoxFragment<?> expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(UnmanagedBoxFragment.class, actual);
    UnmanagedBoxFragment<?> actualFragment = (UnmanagedBoxFragment<?>) actual;
    Assertions.assertEquals(expected.width(Measurement.CONTENT), actualFragment.width(Measurement.CONTENT));
    Assertions.assertEquals(expected.height(Measurement.CONTENT), actualFragment.height(Measurement.CONTENT));
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
    Assertions.assertEquals(
      IntrusiveList._testingOnlySize(expected.fragments()),
      IntrusiveList._testingOnlySize(actualFragment.fragments()));
    
    LayoutFragment curExpected = expected.fragments();
    LayoutFragment curActual = actualFragment.fragments();
    while (curExpected != null) {
      assertFragmentEquals(curExpected, curActual);
      curExpected = curExpected.next();
      curActual = curActual.next();
    }
  }

  private static void assertFragmentEquals(TestFloatRefFragment expected, LayoutFragment actual) {
    Assertions.assertInstanceOf(FloatRefFragment.class, actual);
    FloatRefFragment actualFragment = (FloatRefFragment) actual;
    Assertions.assertEquals(expected.box(), actualFragment.floatFragment().box());
  }

}
