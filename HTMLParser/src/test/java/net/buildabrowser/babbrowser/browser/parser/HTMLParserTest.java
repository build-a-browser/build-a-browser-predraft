package net.buildabrowser.babbrowser.browser.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Text;
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
    Document document = htmlParser.parse(new StringReader("")).immutable();
    Assertions.assertEquals(Document.create(List.of()), document);
  }
  
  @Test
  @DisplayName("Can parse document with text")
  public void canParseDocumentWithText() throws IOException {
    Document document = htmlParser.parse(new StringReader("Hello, World!")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(Text.create("Hello, World!"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with div")
  public void canParseDocumentWithDiv() throws IOException {
    Document document = htmlParser.parse(new StringReader("<div>Hello, World!</div>")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(
        Element.create("div", List.of(
          Text.create("Hello, World!"))))),
      document);
  }

  @Test
  @DisplayName("Can parse document with self closing tag")
  public void canParseDocumentWithSelfClosingTag() throws IOException {
    Document document = htmlParser.parse(new StringReader("<img/><div>Hello, World!</div>")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(
        Element.create("img", List.of()),
        Element.create("div", List.of(
          Text.create("Hello, World!"))))),
      document);
  }

  @Test
  @DisplayName("Can parse document with element with one attribute")
  public void canParseDocumentWithElementWithOneAttribute() throws IOException {
    Document document = htmlParser.parse(new StringReader("<img href=\"file.png\"/>")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(
        Element.create("img", List.of(), Map.of(
          "href", "file.png"
        )))),
      document);
  }

  @Test
  @DisplayName("Can parse document with element with two attributes")
  public void canParseDocumentWithElementWithTwoAttributes() throws IOException {
    Document document = htmlParser.parse(new StringReader("<img href=\"file.png\" alt=\"Image\"/>")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(
        Element.create("img", List.of(), Map.of(
          "href", "file.png",
          "alt", "Image"
        )))),
      document);
  }

  @Test
  @DisplayName("Can parse document with rawtext element")
  public void canParseDocumentWithRawtextElement() throws IOException {
    Document document = htmlParser.parse(new StringReader("<style>p{}</style>")).immutable();
    Assertions.assertEquals(
      Document.create(List.of(
        Element.create("style", List.of(Text.create("p{}")))),
        document.styleSheets()),
      document);
    
    Assertions.assertEquals(1, document.styleSheets().length());
  }

}
