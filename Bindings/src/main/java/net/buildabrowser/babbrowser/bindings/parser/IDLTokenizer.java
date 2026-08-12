package net.buildabrowser.babbrowser.bindings.parser;

import java.io.IOException;
import java.util.Set;

import net.buildabrowser.babbrowser.bindings.parser.token.EOFToken;
import net.buildabrowser.babbrowser.bindings.parser.token.IDLToken;
import net.buildabrowser.babbrowser.bindings.parser.token.IdentToken;
import net.buildabrowser.babbrowser.bindings.parser.token.OtherToken;
import net.buildabrowser.babbrowser.bindings.parser.token.TerminalToken;

public final class IDLTokenizer {

  private static final Set<String> IDL_TERMINALS = Set.of(
    "attribute", "callback", "const", "constructor", "deleter", "dictionary",
    "enum", "getter", "includes", "inherit", "interface", "iterable",
    "maplike", "mixin", "namespace", "partial", "readonly", "required",
    "setlike", "setter", "static", "stringifier", "typedef", "unrestricted",
    "true", "false", "-Infinity", "Infinity", "NaN", "null", "undefined",
    "async_iterable", "or", "sequence", "async_sequence", "object", "symbol",
    "FrozenArray", "ObservableArray", "boolean", "byte", "octet", "bigint",
    "float", "double", "unsigned", "short", "long", "ByteString", "DOMString",
    "USVString", "Promise", "record", "ArrayBuffer", "SharedArrayBuffer",
    "DataView", "Int8Array", "Int16Array", "Int32Array", "Uint8Array",
    "Uint16Array", "Uint32Array", "Uint8ClampedArray", "BigInt64Array",
    "BigUint64Array", "Float16Array", "Float32Array", "Float64Array", "integer",
    "decimal", "identifier", "string", "other", "any", "optional", "[", "]",
    "(", ")", "{", "}", "-", ".", "...", ":", ";", "<", "=", ">", "?", "*", ",");
  
  private IDLTokenizer() {}

  public static IDLTokenStream tokenizeStream(IDLStream stream) {
    return () -> read(stream);
  }

  private static IDLToken read(IDLStream stream) throws IOException {
    int ch = stream.read();
    int postCh = stream.peek();
    while (
      isWhitespace(ch)
      || isComment(ch, postCh)
    ) {
      consumeCommentIfComment(stream, ch, postCh);

      ch = stream.read();
      postCh = stream.peek();
    }

    if (
      ch == '_' && isIdentStart(postCh)
    ) {
      return parseIdent(stream, "_");
    } else if (
      ch == '-' && isIdentStart(postCh)
    ) {
      return parseIdent(stream, "-");
    } else if (isIdentStart(ch)) {
      stream.unread(ch);
      return parseIdent(stream, "");
    } else if (isOtherChar(ch)) {
      stream.unread(ch);
      return parseOther(stream);
    } else if (ch == -1) {
      return EOFToken.create();
    } else {
      System.out.println((char) ch);
      return null; // TODO
    }
  }

  private static IDLToken parseIdent(IDLStream stream, String prefix) throws IOException {
    StringBuilder identBuilder = new StringBuilder(prefix);
    assert isIdentStart(stream.peek());
    identBuilder.appendCodePoint(stream.read());
    while (isIdentChar(stream.peek())) {
      identBuilder.appendCodePoint(stream.read());
    }

    String tokenValue = identBuilder.toString();
    if (isTerminalValue(tokenValue)) {
      return TerminalToken.create(tokenValue);
    }

    return IdentToken.create(identBuilder.toString());
  }

  private static IDLToken parseOther(IDLStream stream) throws IOException {
    StringBuilder otherBuilder = new StringBuilder();
    while (isOtherChar(stream.peek())) {
      otherBuilder.appendCodePoint(stream.read());
    }

    String tokenValue = otherBuilder.toString();
    assert tokenValue.length() > 0;
    if (isTerminalValue(tokenValue)) {
      return TerminalToken.create(tokenValue);
    }

    return OtherToken.create(otherBuilder.toString());
  }

  private static void consumeCommentIfComment(
    IDLStream stream, int ch, int postCh
  ) throws IOException {
    if (isComment(ch, postCh)) {
      stream.read();
      if (postCh == '*') {
        consumeMultiLineCommentTail(stream);
      } else {
        consumeSingleLineCommentTail(stream);
      }
    }
  }

  private static boolean isComment(int ch, int postCh) {
    return
      ch == '/'
      && (postCh == '/' || postCh == '*');
  }

  private static void consumeSingleLineCommentTail(IDLStream stream) throws IOException {
    int ch;
    while (!((ch = stream.read()) == '\n' || ch == -1));
  }

  private static void consumeMultiLineCommentTail(IDLStream stream) throws IOException {
    // TODO: Technically, if the */ is missing, this is not a mutli-line comment
    int ch = stream.read();
    int postCh = stream.peek();
    while (!(
      (ch == '*' && postCh == '/')
      || ch == -1
    )) {
      ch = stream.read();
      postCh = stream.peek();
    }

    stream.read();
  }

  private static boolean isIdentStart(int ch) {
    return
      (ch >= 'A' && ch <= 'Z')
      || (ch >= 'a' && ch <= 'z');
  }

  private static boolean isIdentChar(int ch) {
    return
      isIdentStart(ch)
      || isDigit(ch)
      || (ch == '_' || ch == '-');
  }

  private static boolean isDigit(int ch) {
    return ch >= '0' && ch <= '9';
  }

  private static boolean isOtherChar(int ch) {
    return
      !isWhitespace(ch)
      && !isIdentStart(ch)
      && !isDigit(ch)
      && ch != -1;
  }

  private static boolean isWhitespace(int ch) {
    return switch (ch) {
      case '\t', '\n', '\r', ' ' -> true;
      default -> false;
    };
  }

  private static boolean isTerminalValue(String value) {
    return IDL_TERMINALS.contains(value);
  }

}
