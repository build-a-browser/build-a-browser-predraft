package net.buildabrowser.babbrowser.browser.parser;

import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestComment.testComment;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestElement.testElement;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestText.testText;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestUtil.assertTreeMatches;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestUtil.testDocumentToBody;
import static net.buildabrowser.babbrowser.browser.parser.util.tree.TestUtil.testDocumentToHead;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.htmlparser.HTMLParser;

public class HTMLParserTest {
  
  private HTMLParser htmlParser;

  @BeforeEach
  public void BeforeEach() {
    this.htmlParser = HTMLParser.create();
  }

  @Test
  @DisplayName("Can parse empty document")
  public void canParseEmptyDocument() throws IOException {
    Document document = htmlParser.parse(new StringReader(""));
    assertTreeMatches(testDocumentToBody(), document);
  }
  
  @Test
  @DisplayName("Can parse document with text")
  public void canParseDocumentWithText() throws IOException {
    Document document = htmlParser.parse(new StringReader("Hello, World!"));
    assertTreeMatches(
      testDocumentToBody(testText("Hello, World!")),
      document);
  }

  @Test
  @DisplayName("Can parse document with div")
  public void canParseDocumentWithDiv() throws IOException {
    Document document = htmlParser.parse(new StringReader("<div>Hello, World!</div>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("div", 
          testText("Hello, World!"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with self closing tag")
  public void canParseDocumentWithSelfClosingTag() throws IOException {
    Document document = htmlParser.parse(new StringReader("<img/><div>Hello, World!</div>"));
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
    Document document = htmlParser.parse(new StringReader("<img href=\"file.png\"/>"));
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
    Document document = htmlParser.parse(new StringReader("<img href=\"file.png\" alt=\"Image\"/>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("img", Map.of(
          "href", "file.png",
          "alt", "Image"
        ))),
      document);
  }

  @Test
  @DisplayName("Can parse document with rawtext element")
  public void canParseDocumentWithRawtextElement() throws IOException {
    Document document = htmlParser.parse(new StringReader("<style>p{}</style>"));
    assertTreeMatches(
      testDocumentToHead(
        testElement("style",
          testText("p{}"))),
      document);
    
    Assertions.assertEquals(1, document.styleSheets().length());
  }

  @Test
  @DisplayName("Can parse document with simple doctype")
  public void canParseDocumentWithSimpleDoctype() throws IOException {
    // TODO: DocumentType node
    Document document = htmlParser.parse(new StringReader("<!doctype html>"));
    assertTreeMatches(testDocumentToBody(), document);
  }
  
  @Test
  @DisplayName("Can parse document with comment")
  public void canParseDocumentWithComment() throws IOException {
    Document document = htmlParser.parse(new StringReader("Hello,<!--Cruel--> World!"));
    assertTreeMatches(
      testDocumentToBody(
        testText("Hello,"),
        testComment("Cruel"),
        testText(" World!")),
      document);
  }

}
