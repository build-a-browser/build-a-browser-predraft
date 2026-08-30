package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatValue;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceCollapseValue;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ActiveStylesGeneratorTest {
  
  @Test
  @DisplayName("Can generate active styles with no parent or specified styles")
  public void canGenerateBasicActiveStyles()  {
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf());
    PropertyContainer flattened = activeStyles.flatten(null, a -> a);
    
    // A non-inherited initial property
    CSSValue actual1 = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.CONTENT_BOX, actual1);

    // An inherited initial property
    CSSValue actual2 = flattened.get(CSSProperty.WHITE_SPACE_COLLAPSE);
    Assertions.assertEquals(WhiteSpaceCollapseValue.COLLAPSE, actual2);
  }

  @Test
  @DisplayName("Can generate active styles with single specified style")
  public void canGenerateActiveStylesWithSingleSpecifiedStyle()  {
    WeightedStyleRule testRule = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", false, List.of(
          IdentToken.create("border-box"))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf(testRule));
    PropertyContainer flattened = activeStyles.flatten(null, a -> a);
    
    // A non-inherited initial property
    CSSValue actual1 = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.BORDER_BOX, actual1);
  }

  @Test
  @DisplayName("Can generate active styles with parent")
  public void canGenerateActiveStylesWithParent()  {
    ActiveStyles parent = ActiveStyles.create();
    parent.setProperty(CSSProperty.BOX_SIZING, BoxSizingValue.BORDER_BOX);
    parent.setProperty(CSSProperty.WHITE_SPACE_COLLAPSE, WhiteSpaceCollapseValue.DISCARD);
    PropertyContainer parentContainer = ActiveStyles.unparentedStyles(parent);

    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf());
    PropertyContainer flattened = activeStyles.flatten(parentContainer, a -> a);
    
    // A non-inherited initial property
    CSSValue actual1 = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.CONTENT_BOX, actual1);

    // An inherited initial property
    CSSValue actual2 = flattened.get(CSSProperty.WHITE_SPACE_COLLAPSE);
    Assertions.assertEquals(WhiteSpaceCollapseValue.DISCARD, actual2);
  }

  @Test
  @DisplayName("Can generate active styles with shadowed parent")
  public void canGenerateActiveStylesWithShadowedParent()  {
    ActiveStyles parent = ActiveStyles.create();
    parent.setProperty(CSSProperty.WHITE_SPACE_COLLAPSE, WhiteSpaceCollapseValue.DISCARD);
    PropertyContainer parentContainer = ActiveStyles.unparentedStyles(parent);

    WeightedStyleRule testRule = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "white-space-collapse", false, List.of(
          IdentToken.create("preserve-spaces"))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf(testRule));
    PropertyContainer flattened = activeStyles.flatten(parentContainer, a -> a);

    CSSValue actual2 = flattened.get(CSSProperty.WHITE_SPACE_COLLAPSE);
    Assertions.assertEquals(WhiteSpaceCollapseValue.PRESERVE_SPACES, actual2);
  }

  @Test
  @DisplayName("Can generate active styles with parent and unset/inital/inherit")
  public void canGenerateActiveStylesWithParentAndUnsetInitialInherit()  {
    ActiveStyles parent = ActiveStyles.create();
    parent.setProperty(CSSProperty.BOX_SIZING, BoxSizingValue.BORDER_BOX);
    parent.setProperty(CSSProperty.WHITE_SPACE_COLLAPSE, WhiteSpaceCollapseValue.DISCARD);
    parent.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.CENTER);
    PropertyContainer parentContainer = ActiveStyles.unparentedStyles(parent);

    WeightedStyleRule testRule1 = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", false, List.of(
          IdentToken.create("content-box"))),
      createTestDeclaration(
        "white-space-collapse", false, List.of(
          IdentToken.create("preserve-spaces"))),
      createTestDeclaration(
        "text-align", false, List.of(
          IdentToken.create("end"))));
    WeightedStyleRule testRule2 = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", false, List.of(
          IdentToken.create("inherit"))),
      createTestDeclaration(
        "white-space-collapse", false, List.of(
          IdentToken.create("initial"))),
      createTestDeclaration(
        "text-align", false, List.of(
          IdentToken.create("unset"))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(
      listOf(testRule1, testRule2));
    PropertyContainer flattened = activeStyles.flatten(parentContainer, a -> a);
    
    CSSValue actual1 = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.BORDER_BOX, actual1);
    CSSValue actual2 = flattened.get(CSSProperty.WHITE_SPACE_COLLAPSE);
    Assertions.assertEquals(WhiteSpaceCollapseValue.COLLAPSE, actual2);
    CSSValue actual3 = flattened.get(CSSProperty.TEXT_ALIGN);
    Assertions.assertEquals(TextAlignValue.CENTER, actual3);
  }

  @Test
  @DisplayName("Can generate active styles with invalid specified style")
  public void canGenerateActiveStylesWithInvalidSpecifiedStyle()  {
    WeightedStyleRule testRule = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", false, List.of(
          IdentToken.create("margin-box"))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf(testRule));
    PropertyContainer flattened = activeStyles.flatten(null, a -> a);
    
    // Important
    CSSValue actual = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.CONTENT_BOX, actual);
  }

  @Test
  @DisplayName("Can generate active styles with important specified style")
  public void canGenerateActiveStylesWithImportantSpecifiedStyle()  {
    WeightedStyleRule testRule1 = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", true, List.of(
          IdentToken.create("border-box"))),
      createTestDeclaration(
        "text-align", false, List.of(
          IdentToken.create("left"))));
    WeightedStyleRule testRule2 = createTestRule(
      SelectorSpecificity.ZERO_SPECIFICITY,
      createTestDeclaration(
        "box-sizing", false, List.of(
          IdentToken.create("content-box"))),
      createTestDeclaration(
        "text-align", false, List.of(
          IdentToken.create("center"))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(
      listOf(testRule1, testRule2));
    PropertyContainer flattened = activeStyles.flatten(null, a -> a);
    
    // Important
    CSSValue actual1 = flattened.get(CSSProperty.BOX_SIZING);
    Assertions.assertEquals(BoxSizingValue.BORDER_BOX, actual1);

    // Not Important
    CSSValue actual2 = flattened.get(CSSProperty.TEXT_ALIGN);
    Assertions.assertEquals(TextAlignValue.CENTER, actual2);
  }

  @DisplayName("Can generate active styles with parent and unset/inital/inherit")
  public void canGenerateActiveStylessSortedBySpecificity()  {
    ActiveStyles parent = ActiveStyles.create();
    parent.setProperty(CSSProperty.BOX_SIZING, BoxSizingValue.BORDER_BOX);
    parent.setProperty(CSSProperty.WHITE_SPACE_COLLAPSE, WhiteSpaceCollapseValue.DISCARD);
    parent.setProperty(CSSProperty.TEXT_ALIGN, TextAlignValue.CENTER);
    PropertyContainer parentContainer = ActiveStyles.unparentedStyles(parent);

    WeightedStyleRule testRule1 = createTestRule(
      new SelectorSpecificity(1, 1, 0),
      createTestDeclaration(
        "margin-left", false, List.of(
          DimensionToken.create(4, "px"))),
      createTestDeclaration(
        "margin-right", false, List.of(
          DimensionToken.create(5, "px"))));
    WeightedStyleRule testRule2 = createTestRule(
      new SelectorSpecificity(2, 0, 0),
      createTestDeclaration(
        "margin-left", false, List.of(
          DimensionToken.create(6, "px"))));
    WeightedStyleRule testRule3 = createTestRule(
      new SelectorSpecificity(1, 0, 0),
      createTestDeclaration(
        "margin-left", false, List.of(
          DimensionToken.create(1, "px"))),
      createTestDeclaration(
        "margin-right", false, List.of(
          DimensionToken.create(2, "px"))),
      createTestDeclaration(
        "margin-top", false, List.of(
          DimensionToken.create(3, "px"))));
      
    List<WeightedStyleRule> rules = new ArrayList<>(3);
    rules.add(testRule1);
    rules.add(testRule2);
    rules.add(testRule3);
    rules.sort(WeightedStyleRule::compare);

    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(rules);
    PropertyContainer flattened = activeStyles.flatten(parentContainer, a -> a);
    
    CSSValue actual1 = flattened.get(CSSProperty.MARGIN_LEFT);
    Assertions.assertEquals(LengthValue.create(6, true, LengthType.PX), actual1);
    CSSValue actual2 = flattened.get(CSSProperty.MARGIN_RIGHT);
    Assertions.assertEquals(LengthValue.create(5, true, LengthType.PX), actual2);
    CSSValue actual3 = flattened.get(CSSProperty.MARGIN_LEFT);
    Assertions.assertEquals(LengthValue.create(3, true, LengthType.PX), actual3);
  }

  @Test
  @DisplayName("Can generate active styles with resolvable CSS variable")
  public void canGenerateActiveStylesWithResolvableCSSVariable()  {
    ActiveStyles parent = ActiveStyles.create();
    parent.setCustomProperty("--hello", new CSSVarValue(
      List.of(IdentToken.create("left"))));
    PropertyContainer parentContainer = ActiveStyles.unparentedStyles(parent);

    WeightedStyleRule testRule = createTestRule(
      new SelectorSpecificity(2, 0, 0),
      createTestDeclaration(
        "float", false, List.of(
          new FunctionValue("var", List.of(
            IdentToken.create("--hello"),
            CommaToken.create(),
            IdentToken.create("right"))))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf(testRule));
    PropertyContainer flattened = activeStyles.flatten(parentContainer, a -> a);
    
    CSSValue actual = flattened.get(CSSProperty.FLOAT);
    Assertions.assertEquals(FloatValue.LEFT, actual);
  }

  @Test
  @DisplayName("Can generate active styles with fallback CSS variable")
  public void canGenerateActiveStylesWithFallbackCSSVariable()  {
    WeightedStyleRule testRule = createTestRule(
      new SelectorSpecificity(2, 0, 0),
      createTestDeclaration(
        "float", false, List.of(
          new FunctionValue("var", List.of(
            IdentToken.create("--hello"),
            CommaToken.create(),
            IdentToken.create("right"))))));
    ActiveStyles activeStyles = ActiveStylesGenerator.generateActiveStyles(listOf(testRule));
    PropertyContainer flattened = activeStyles.flatten(null, a -> a);
    
    CSSValue actual = flattened.get(CSSProperty.FLOAT);
    Assertions.assertEquals(FloatValue.RIGHT, actual);
  }

  private List<WeightedStyleRule> listOf(WeightedStyleRule... rules) {
    List<WeightedStyleRule> rules2 = new ArrayList<>();
    rules2.addAll(List.of(rules));

    return rules2;
  }

  private static WeightedStyleRule createTestRule(
    SelectorSpecificity specificity,
    Declaration... declarations
  ) {
    StyleRule rule = new StyleRule(List.of(declarations));
    return WeightedStyleRule.create(
      rule, specificity,
      SelectorTarget.ELEMENT,
      RuleSource.AUTHOR,
      null, 0, 0);
  }

  private static Declaration createTestDeclaration(
    String name,
    boolean important,
    List<Token> value
  ) {
    return Declaration.create(null, name, value, important);
  }

}