package net.buildabrowser.babbrowser.bindings.parser;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.bindings.parser.token.EOFToken;
import net.buildabrowser.babbrowser.bindings.parser.token.IDLToken;
import net.buildabrowser.babbrowser.bindings.parser.token.IdentToken;
import net.buildabrowser.babbrowser.bindings.parser.token.OtherToken;
import net.buildabrowser.babbrowser.bindings.parser.token.TerminalToken;

public class IDLTokenizerTest {
  
  @Test
  @DisplayName("Can tokenize simple ident")
  public void canTokenizeSimpleIdent() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("ident"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(IdentToken.create("ident"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can ignore whitespace")
  public void canIgnoreWhitespace() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("\t\n\r ident"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(IdentToken.create("ident"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can ignore single-line comments")
  public void canIgnoreSingleLineComments() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("//ignored\nident"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(IdentToken.create("ident"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can ignore multi-line comments")
  public void canIgnoreMultiLineComments() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("/*ignored\n*/ident"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(IdentToken.create("ident"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can tokenize terminal")
  public void canTokenizeTerminal() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("interface"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(TerminalToken.create("interface"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can tokenize other")
  public void canTokenizeOther() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("(.)"));
    IDLToken actual = stream.read();
    Assertions.assertEquals(OtherToken.create("(.)"), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

  @Test
  @DisplayName("Can tokenize other terminal")
  public void canTokenizeOtherTerminal() throws IOException {
    IDLTokenStream stream = IDLTokenizer.tokenizeStream(
      IDLStream.create("."));
    IDLToken actual = stream.read();
    Assertions.assertEquals(TerminalToken.create("."), actual);
    Assertions.assertEquals(EOFToken.create(), stream.read());
  }

}
