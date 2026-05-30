package net.buildabrowser.babbrowser.htmlparser;

import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestElement.testElement;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestText.testText;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestUtil.assertTreeMatches;
import static net.buildabrowser.babbrowser.htmlparser.util.tree.TestUtil.testDocumentToBody;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.dom.Document;

public class HTMLParserTablesTest {
  
  @Test
  @DisplayName("Can parse document with empty table")
  public void canParseDocumentWithEmptyTable() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<table>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table")),
      document);
  }

  @Test
  @DisplayName("Can parse document with table with tbody")
  public void canParseDocumentWithTableWithTbody() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<table><tbody>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody"))),
      document);
  }

  @Test
  @DisplayName("Can parse document with table with tbody and tr")
  public void canParseDocumentWithTableWithTbodyAndTr() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<table><tbody><tr>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody",
            testElement("tr")))),
      document);
  }

  @Test
  @DisplayName("Can parse document with table with tbody, tr, and td")
  public void canParseDocumentWithTableWithTbodyTrAndTd() throws IOException {
    Document document = HTMLParser.parse(new StringReader("<table><tbody><tr><td>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody",
            testElement("tr",
              testElement("td"))))),
      document);
  }

  @Test
  @DisplayName("Can parse document with table with tbody, tr, td, and text")
  public void canParseDocumentWithTableWithTbodyTrTdAndText() throws IOException {
    Document document = HTMLParser.parse(new StringReader(
      "<table><tbody><tr><td>Hello, World!</td></tr></tbody></table>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody",
            testElement("tr",
              testElement("td",
                testText("Hello, World!")))))),
      document);
  }

  @Test
  @DisplayName("Can parse document with table with td, and text")
  public void canParseDocumentWithTableWithTdAndText() throws IOException {
    Document document = HTMLParser.parse(new StringReader(
      "<table><td>Hello, World!</td></table>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody",
            testElement("tr",
              testElement("td",
                testText("Hello, World!")))))),
      document);
  }

  /* Funny thing, this was causing a bug on kernel.org where every td was wrapped by its own tr and tbody */
  @Test
  @DisplayName("Can parse document with table with whitespace, tr, two td, and two text")
  public void canParseDocumentWithTableWithWhitespaceTwoTdAndTwoText() throws IOException {
    Document document = HTMLParser.parse(new StringReader(
      "<table> <tr> <td>You say Hello</td> <td>And I say Goodbye</td> </tr> </table>"));
    assertTreeMatches(
      testDocumentToBody(
        testElement("table",
          testElement("tbody",
            testElement("tr",
              testElement("td",
                testText("You say Hello")),
              testElement("td",
                testText("And I say Goodbye")))))),
      document);
  }

  // TODO: Tests for thead, th

}
