package net.buildabrowser.babbrowser.renderer.layout;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.font.FontFamilyValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontNameValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontNamedSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightValue.RelativeFontWeightValue;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader.FontFamily;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader.FontOptions;

public final class FontDetermination {
  
  private FontDetermination() {}

  public static FontDeterminationContext determineFont(
    FontDeterminationContext parentDetermination,
    ActiveStyles activeStyles,
    LayoutContext parentContext
  ) {
    float fontSize = parentDetermination.fontSize();
    if (!activeStyles.wasInherited(CSSProperty.FONT_SIZE)) {
      fontSize = determineNewFontSize(fontSize, activeStyles, parentContext);
    }

    int fontWeight = parentDetermination.fontWeight();
    if (!activeStyles.wasInherited(CSSProperty.FONT_WEIGHT)) {
      fontWeight = determineNewFontWeight(fontWeight, activeStyles);
    }

    ManyResult fontFamily = (ManyResult) activeStyles.getProperty(CSSProperty.FONT_FAMILY);

    boolean wasChanged =
      fontSize != parentDetermination.fontSize()
      || fontWeight != parentDetermination.fontWeight()
      || !fontFamily.equals(parentDetermination.fontFamily());

    LoadedFont font = parentDetermination.font();
    if (wasChanged) {
      FontLoader fontLoader = parentContext.global().resourceLoader().fontLoader();
      List<FontFamily> fontFamilies = collectFamilies(fontLoader, fontFamily);
      FontOptions options = new FontOptions(fontFamilies, fontSize, fontWeight);
      font = parentContext.global().fontCache().load(options);
    }

    return new FontDeterminationContext(font, fontSize, fontWeight, fontFamily);
  }

  private static float determineNewFontSize(
    float parentSize,
    ActiveStyles activeStyles,
    LayoutContext parentContext
  ) {
    CSSValue fontSizeValue = activeStyles.getProperty(CSSProperty.FONT_SIZE);
    if (fontSizeValue instanceof FontNamedSizeValue fontNamedSizeValue) {
      if (fontNamedSizeValue.isAbsolute()) {
        return parentContext.global().rootMetrics().size() * fontNamedSizeValue.scaling();
      } else {
        // TODO: Check if it matches an absolute anyways
        return parentSize * fontNamedSizeValue.scaling();
      }
    } else {
      LayoutConstraint intendedSize = SizingUtil.evaluateBaseSize(
        parentContext, LayoutConstraint.of(parentSize), fontSizeValue);
      assert intendedSize.isBounded();
      return intendedSize.value();
    }
  }

  private static int determineNewFontWeight(int fontWeight, ActiveStyles activeStyles) {
    CSSValue fontWeightValue = activeStyles.getProperty(CSSProperty.FONT_WEIGHT);
    if (fontWeightValue.equals(RelativeFontWeightValue.BOLDER)) {
      return bolder(fontWeight);
    } else if (fontWeightValue.equals(RelativeFontWeightValue.LIGHTER)) {
      return lighter(fontWeight);
    }

    assert fontWeightValue instanceof FontWeightValue;
    return ((FontWeightValue) fontWeightValue).weight();
  }

  private static int bolder(int boldness) {
    if (boldness < 350) {
      return 400;
    } else if (boldness < 550) {
      return 700;
    } else if (boldness < 900) {
      return 900;
    } else {
      return boldness;
    }
  }

  private static int lighter(int boldness) {
    if (boldness < 100) {
      return boldness;
    } else if (boldness < 550) {
      return 100;
    } else if (boldness < 750) {
      return 400;
    } else {
      return 700;
    }
  }

  private static List<FontFamily> collectFamilies(FontLoader fontLoader, ManyResult familyIds) {
    List<FontFamily> families = new ArrayList<>(familyIds.values().size());
    for (CSSValue value: familyIds.values()) {
      if (value instanceof FontNameValue fontNameValue) {
        families.add(fontLoader.named(fontNameValue.name()));
      } else if (value.equals(FontFamilyValue.MONOSPACE)) {
        families.add(fontLoader.monospace());
      } else if (value.equals(FontFamilyValue.SERIF)) {
        families.add(fontLoader.serif());
      } else if (value.equals(FontFamilyValue.SANS_SERIF)) {
        families.add(fontLoader.sansSerif());
      } else {
        throw new UnsupportedOperationException("Don't recognize supplied CSS value!");
      }
    }

    // TODO: Replace this with a proper font loading system
    families.add(fontLoader.sansSerif());
    return families;
  }

  public static record FontDeterminationContext(
    LoadedFont font,
    float fontSize,
    int fontWeight,
    ManyResult fontFamily
  ) {}

}
