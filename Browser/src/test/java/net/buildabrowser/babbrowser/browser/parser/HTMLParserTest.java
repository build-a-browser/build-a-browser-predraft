package net.buildabrowser.babbrowser.browser.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.browser.dom.Document;
import net.buildabrowser.babbrowser.browser.dom.Element;
import net.buildabrowser.babbrowser.browser.dom.Text;

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
    Assertions.assertEquals(new Document(List.of()), document);
  }
  
  @Test
  @DisplayName("Can parse document with text")
  public void canParseDocumentWithText() throws IOException {
    Document document = htmlParser.parse(new StringReader("Hello, World!"));
    Assertions.assertEquals(
      new Document(List.of(new Text("Hello, World!"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with div")
  public void canParseDocumentWithDiv() throws IOException {
    Document document = htmlParser.parse(new StringReader("<div>Hello, World!</div>"));
    Assertions.assertEquals(
      new Document(List.of(
        new Element("div", List.of(
          new Text("Hello, World!"))))),
      document);
  }

}
