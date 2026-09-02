package net.buildabrowser.babbrowser.htmlparser;

import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestComment.testComment;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestElement.testElement;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestText.testText;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestUtil.assertTreeMatches;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestUtil.testDocumentToBody;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestUtil.testDocumentToHead;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;

public class HTMLParserTest {

  @Test
  @DisplayName("Can parse empty document")
  public void canParseEmptyDocument() throws IOException {
    Document document = HTMLParser.parse(new StringReader(""));
    assertTreeMatches(testDocumentToBody(), document);
  }
  
  @Test
  @DisplayName("Can parse document with text")
  public void canParseDocumentWithText() throws IOException {
    Document document = HTMLParser.parse(new StringReader("Hello, World!"));
    assertTreeMatches(
      testDocumentToBody(testText("Hello, World!")),
      document);
  }

  @Test
  @DisplayName("Can parse document with div")
  public void canParseDocumentWithDiv() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<div>Hello, World!</div>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("div", 
          testText("Hello, World!"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with self closing tag")
  public void canParseDocumentWithSelfClosingTag() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<img/><div>Hello, World!</div>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("img"),
        testElement("div", 
          testText("Hello, World!"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with element with one attribute")
  public void canParseDocumentWithElementWithOneAttribute() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<img href=\"file.png\"/>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("img", Map.of(
          "href", "file.png"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with element with two attributes")
  public void canParseDocumentWithElementWithTwoAttributes() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<img href=\"file.png\" alt=\"Image\"/>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("img", Map.of(
          "href", "file.png",
          "alt", "Image"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with element with unquoted attribute")
  public void canParseDocumentWithElementWithUnquotedAttribute() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<span class=gold></span>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("span", Map.of(
          "class", "gold"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with rawtext element")
  public void canParseDocumentWithRawtextElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<style>p{}</style>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("style",
          testText("p{}"))),
      document);
    
    Assertions.assertEquals(1, document.styleSheets().length());
  }

  @Test
  @DisplayName("Can parse document with rcdata element")
  public void canParseDocumentWithRcdataElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<title>Less &lt; Test</title>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("title",
          testText("Less < Test"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with script element")
  public void canParseDocumentWithScriptElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<script>let x = a < b</script>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("script",
          testText("let x = a < b"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with script escaped element")
  public void canParseDocumentWithScriptEscapedElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<script><!--let x = a < b--></script>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("script",
          testText("<!--let x = a < b-->"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with script escaped element")
  public void canParseDocumentWithScriptDoubleEscapedElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<script><!--<script>let x = a < b</script>--></script>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("script",
          testText("<!--<script>let x = a < b</script>-->"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with template element")
  public void canParseDocumentWithTemplateElement() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<template></template>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("template")),
      document);
  }

  @Test
  @DisplayName("Can parse document with self-closing meta tag")
  public void canParseDocumentWithSelfClosingMetaTag() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<meta/>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("meta")),
      document);
  }

  // TODO: Eventually, add a proper doctype node
  @Test
  @DisplayName("Can parse document with simple doctype")
  public void canParseDocumentWithSimpleDoctype() throws IOException {
    // TODO: DocumentType node
    Document document = HTMLParser.parse(new StringReader("<!doctype html>"));
    assertTreeMatches(testDocumentToBody(), document);
  }

  @Test
  @DisplayName("Can parse document with doctype with public identifier")
  public void canParseDocumentWithDoctypeWithPublicIdentifier() throws IOException {
    // TODO: DocumentType node
    Document document = HTMLParser.parse(new StringReader(
      "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">"));
    assertTreeMatches(testDocumentToBody(), document);
  }

  @Test
  @DisplayName("Can parse document with doctype with public then system identifier")
  public void canParseDocumentWithDoctypeWithPublicThenSystemIdentifier() throws IOException {
    // TODO: DocumentType node
    Document document = HTMLParser.parse(new StringReader(
      "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\" \"http://www.w3.org/TR/REC-html40/strict.dtd\">"));
    assertTreeMatches(testDocumentToBody(), document);
  }
  
  @Test
  @DisplayName("Can parse document with comment")
  public void canParseDocumentWithComment() throws IOException {
    Document document = HTMLParser.parse(new StringReader("Hello,<!--Cruel--> World!"));
    assertTreeMatches(
      testDocumentToBody(
        testText("Hello,"),
        testComment("Cruel"),
        testText(" World!")),
      document);
  }

  @Test
  @DisplayName("Can parse document with invalid named character reference")
  public void canParseDocumentWithInvalidNamedCharacterReference() throws IOException {
    Document document = HTMLParser.parse(new StringReader("&invalid;"));
    assertTreeMatches(
      testDocumentToBody(testText("&invalid;")),
      document);
  }

  @Test
  @DisplayName("Can parse document with valid named character reference")
  public void canParseDocumentWithValidNamedCharacterReference() throws IOException {
    Document document = HTMLParser.parse(new StringReader("&lt;"));
    assertTreeMatches(
      testDocumentToBody(testText("<")),
      document);
  }

  @Test
  @DisplayName("Can parse document with valid decimal character reference")
  public void canParseDocumentWithValidDecimalCharacterReference() throws IOException {
    Document document = HTMLParser.parse(new StringReader("&#60;"));
    assertTreeMatches(
      testDocumentToBody(testText("<")),
      document);
  }

  @Test
  @DisplayName("Can parse document with valid hexdecimal character reference")
  public void canParseDocumentWithValidHexadecimalCharacterReference() throws IOException {
    Document document = HTMLParser.parse(new StringReader("&#x3C;"));
    assertTreeMatches(
      testDocumentToBody(testText("<")),
      document);
  }

  @Test
  @DisplayName("Can parse document with invalid named character reference in attribute")
  public void canParseDocumentWithInvalidNamedCharacterReferenceInAttribute() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<a href=\"&invalid;\"></a>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("a", Map.of(
          "href", "&invalid;"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with valid named character reference in attribute")
  public void canParseDocumentWithValidNamedCharacterReferenceInAttribute() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<a href=\"&lt;\"></a>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("a", Map.of(
          "href", "<"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with implied end tags")
  public void canParseDocumentWithImpliedEndTags() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<p>Text<p>Text 2"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("p", testText("Text")),
        testElement("p", testText("Text 2"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with implied dd/dt end tags")
  public void canParseDocumentWithImpliedDdDtEndTags() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<dd>Text<dt>Text 2<dd>Text 3"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("dd", testText("Text")),
        testElement("dt", testText("Text 2")),
        testElement("dd", testText("Text 3"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with implied li end tags")
  public void canParseDocumentWithImpliedLiEndTags() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<li>Text<li>Text 2"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("li", testText("Text")),
        testElement("li", testText("Text 2"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with self-closing hr tags")
  public void canParseDocumentWithSelfClosingHrTags() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<hr/>Text<hr/>Text 2"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("hr"),
        testText("Text"),
        testElement("hr"),
        testText("Text 2")),
      document);
  }

  @Test
  @DisplayName("Can parse document with carriage returns")
  public void canParseDocumentWithCarriageReturns() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<span>\r\r\n\n</span>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("span",
          testText("\n\n\n"))),
      document);
  }

}
