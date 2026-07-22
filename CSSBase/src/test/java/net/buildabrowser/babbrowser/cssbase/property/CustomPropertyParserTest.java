package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.intermediate.FunctionValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.test.TestPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class CustomPropertyParserTest {

  private static final CSSTokenStreamSource TEST_SOURCE = new CSSTokenStreamSource(
    CommonUtil.rethrow(() -> new URI("about:blank")));

  //#region isValidCustomPropertyValue

  @Test
  @DisplayName("Custom property value is valid for empty value")
  public void customPropertyValueIsValidForEmptyValue() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting();
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertTrue(isValid);
  }

  @Test
  @DisplayName("Custom property value is valid for typical value")
  public void customPropertyValueIsValidForTypicalValue() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      DimensionToken.create(2, "em"),
      IdentToken.create("solid"),
      HashToken.create("000000", HashToken.Type.UNRESTRICTED)
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertTrue(isValid);
  }

  @Test
  @DisplayName("Custom property value is not valid for value with unbalanced right parentheses")
  public void customPropertyValueIsNotValidForValueWithUnbalancedRightParantheses() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      RParenToken.create()
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertFalse(isValid);
  }

  @Test
  @DisplayName("Custom property value is valid for nested matched brackets")
  public void customPropertyValueIsValidForNestedMatchedBrackets() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      LSBracketToken.create(),
      LCBracketToken.create(),
      RCBracketToken.create(),
      RSBracketToken.create()
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertTrue(isValid);
  }

  @Test
  @DisplayName("Custom property value is not valid for misnested brackets")
  public void customPropertyValueIsNotValidForMisnestedBrackets() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      LSBracketToken.create(),
      LCBracketToken.create(),
      RSBracketToken.create(),
      RCBracketToken.create()
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertFalse(isValid);
  }

  @Test
  @DisplayName("Custom property value validity check is recursive")
  public void customPropertyValueValidityCheckIsRecursive() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("func", List.of(RParenToken.create()))
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertFalse(isValid);
  }

  @Test
  @DisplayName("Custom property value is not valid for value with top-level exclamation")
  public void customPropertyValueIsNotValidForValueWithTopLevelExclamation() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      DelimToken.create('!')
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertFalse(isValid);
  }

  @Test
  @DisplayName("Custom property value is valid for value with nested exclamation")
  public void customPropertyValueIsValidForValueWithNestedExclamation() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("func", List.of(DelimToken.create('!')))
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertTrue(isValid);
  }

  @Test
  @DisplayName("Custom property value is not valid for value with malformed var reference")
  public void customPropertyValueIsNotValidForValueWithMalformedVarReference() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("var", List.of())
    );
    boolean isValid = CustomPropertyParser.isValidCustomPropertyValue(tokens, true);
    Assertions.assertFalse(isValid);
  }

  //#region hasVarReferences

  @Test
  @DisplayName("Var reference is detected when present")
  public void varReferenceIsDetectedWhenPresent() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("var", List.of(IdentToken.create("--my-var")))
    );
    Boolean isDetected = CustomPropertyParser.hasVarReferences(tokens);
    Assertions.assertNotNull(isDetected);
    Assertions.assertTrue(isDetected);
  }

  @Test
  @DisplayName("Var reference is not detected when not present")
  public void varReferenceIsNotDetectedWhenNotPresent() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("fast", List.of(IdentToken.create("car")))
    );
    Boolean isDetected = CustomPropertyParser.hasVarReferences(tokens);
    Assertions.assertNotNull(isDetected);
    Assertions.assertFalse(isDetected);
  }

  @Test
  @DisplayName("Var reference is detected when nested")
  public void varReferenceIsDetectedWhenNested() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("far", List.of(
        new FunctionValue("var", List.of(IdentToken.create("--my-var")))
      ))
    );
    Boolean isDetected = CustomPropertyParser.hasVarReferences(tokens);
    Assertions.assertNotNull(isDetected);
    Assertions.assertTrue(isDetected);
  }

  @Test
  @DisplayName("Invalid var reference is reported")
  public void invalidVarReferenceIsReported() throws IOException {
    SeekableCSSTokenStream tokens = CSSTokenStream.createForTesting(
      new FunctionValue("var", List.of(IdentToken.create("my-var")))
    );
    Boolean isDetected = CustomPropertyParser.hasVarReferences(tokens);
    Assertions.assertNull(isDetected);
  }

  //#region resolveVarValues

  @Test
  @DisplayName("Can resolve simple var reference")
  public void canResolveSimpleVarReference() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    refContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        IdentToken.create("CAFEBABE"))));
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(IdentToken.create("--secret-code")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("CAFEBABE")
    )), resultValue);
  }

  @Test
  @DisplayName("Can resolve nested var reference")
  public void canResolveNestedVarReference() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    refContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        IdentToken.create("CAFEBABE"))));
    Declaration declaration = asDeclaration(
      new FunctionValue("nested", List.of(
        new FunctionValue("var", List.of(IdentToken.create("--secret-code")))))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      new FunctionValue("nested", List.of(
        IdentToken.create("CAFEBABE")))
    )), resultValue);
  }

  @Test
  @DisplayName("Can resolve var reference with var and fallback")
  public void canResolveVarReferenceWithVarAndFallback() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    refContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        IdentToken.create("CAFEBABE"))));
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(
        IdentToken.create("--secret-code"),
        CommaToken.create(),
        IdentToken.create("CAFEDEAD")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("CAFEBABE")
    )), resultValue);
  }

  @Test
  @DisplayName("Can resolve var reference with just fallback")
  public void canResolveVarReferenceWithJustFallback() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(
        IdentToken.create("--secret-code"),
        CommaToken.create(),
        IdentToken.create("CAFEDEAD")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("CAFEDEAD")
    )), resultValue);
  }
  
  @Test
  @DisplayName("Can resolve inherited var reference")
  public void canResolveInheritedVarReference() throws IOException {
    TestPropertyContainer parentRefContainer = new TestPropertyContainer(null);
    parentRefContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        IdentToken.create("CAFEBABE"))));
    TestPropertyContainer refContainer = new TestPropertyContainer(parentRefContainer);
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(IdentToken.create("--secret-code")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("CAFEBABE")
    )), resultValue);
  }
  
  @Test
  @DisplayName("Cannot resolve initial var reference")
  public void cannotResolveInitialVarReference() throws IOException {
    TestPropertyContainer parentRefContainer = new TestPropertyContainer(null);
    parentRefContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        IdentToken.create("CAFEBABE"))));
    TestPropertyContainer refContainer = new TestPropertyContainer(parentRefContainer);
    refContainer.setCustomProperty("--secret-code", null); // Acts like the initial keyword
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(IdentToken.create("--secret-code")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertTrue(resultValue.isFailure());
  }

  @Test
  @DisplayName("Cannot resolve unspecified var reference")
  public void cannotResolveUnspecifiedVarReference() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(IdentToken.create("--secret-code")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertTrue(resultValue.isFailure());
  }
  
  @Test
  @DisplayName("Can resolve var reference with var reference")
  public void canResolveVarReferenceWithVarReference() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    refContainer.setCustomProperty(
      "--a-name", new CSSVarValue(List.of(
        IdentToken.create("johhny"))));
    refContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        new FunctionValue("var", List.of(
          IdentToken.create("--a-name"))))));
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(IdentToken.create("--secret-code")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("johhny")
    )), resultValue);
  }
  
  @Test
  @DisplayName("Falls back on recursive var reference")
  public void fallsBackOnRecursiveVarReference() throws IOException {
    TestPropertyContainer refContainer = new TestPropertyContainer(null);
    refContainer.setCustomProperty(
      "--a-name", new CSSVarValue(List.of(
        new FunctionValue("var", List.of(
          IdentToken.create("--secret-code"))))));
    refContainer.setCustomProperty(
      "--secret-code", new CSSVarValue(List.of(
        new FunctionValue("var", List.of(
          IdentToken.create("--a-name"),
          CommaToken.create(),
          IdentToken.create("test"))))));
    Declaration declaration = asDeclaration(
      new FunctionValue("var", List.of(
        IdentToken.create("--secret-code"),
        CommaToken.create(),
        IdentToken.create("CAFEDEAD")))
    );
    CSSValue resultValue = CustomPropertyParser.resolveVarValues(
      TEST_SOURCE, declaration, refContainer);
    Assertions.assertEquals(new CSSVarValue(List.of(
      IdentToken.create("CAFEDEAD")
    )), resultValue);
  }

  private Declaration asDeclaration(Token... tokens) {
    return Declaration.create(TEST_SOURCE, "test", List.of(tokens), false);
  }

}
