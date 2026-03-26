package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class NumericCharacterReferenceEndState implements TokenizeState {

  private static final int[] remaps = new int[0xA0];

  static {
    remaps[0x80] = 0x20AC;
    remaps[0x82] = 0x201A;
    remaps[0x83] = 0x0192;
    remaps[0x84] = 0x201E;
    remaps[0x85] = 0x2026;
    remaps[0x86] = 0x2020;
    remaps[0x87] = 0x2021;
    remaps[0x88] = 0x02C6;
    remaps[0x89] = 0x2030;
    remaps[0x8A] = 0x0160;
    remaps[0x8B] = 0x2039;
    remaps[0x8C] = 0x0152;
    remaps[0x8E] = 0x017D;
    remaps[0x91] = 0x2018;
    remaps[0x92] = 0x2019;
    remaps[0x93] = 0x201C;
    remaps[0x94] = 0x201D;
    remaps[0x95] = 0x2022;
    remaps[0x96] = 0x2013;
    remaps[0x97] = 0x2014;
    remaps[0x98] = 0x02DC;
    remaps[0x99] = 0x2122;
    remaps[0x9A] = 0x0161;
    remaps[0x9B] = 0x203A;
    remaps[0x9C] = 0x0153;
    remaps[0x9E] = 0x017E;
    remaps[0x9F] = 0x0178;
  }

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    int charRefCode = tokenizeContext.getCharacterReferenceCode();
    // Character.isSurrogate requires a char, but we have an int
    if (charRefCode == 0 || charRefCode >= 0x10FFFF || isSurrogate(charRefCode)) {
      parseContext.parseError();
      charRefCode = 0xFFFD;
    } else if (isNonCharacter(charRefCode)) {
      parseContext.parseError();
    } else if (remaps[charRefCode] != 0) {
      charRefCode = remaps[charRefCode];
    }

    tokenizeContext.temporaryBuffer().clear();
    tokenizeContext.temporaryBuffer().append(charRefCode);
    tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
    // I think this reconsumes
    tokenizeContext.reconsumeInTokenizeState(ch, tokenizeContext.getReturnState());
  }

  private boolean isSurrogate(int ch) {
    return
      (ch >= 0xD800 && ch <= 0xDBFF)
      || (ch >= 0xDC00 && ch <= 0xDFFF);
  }
  
  private boolean isNonCharacter(int ch) {
    if (ch >= 0xFDD0 && ch <= 0xFDEF) return true;
    return switch (ch) {
      case 0xFFFE, 0XFFFF, 0X1FFFE, 0X1FFFF, 0X2FFFE, 0X2FFFF, 0X3FFFE, 0X3FFFF, 0X4FFFE, 0X4FFFF, 0X5FFFE,
        0X5FFFF, 0X6FFFE, 0X6FFFF, 0X7FFFE, 0X7FFFF, 0X8FFFE, 0X8FFFF, 0X9FFFE, 0X9FFFF, 0XAFFFE, 0XAFFFF,
        0XBFFFE, 0XBFFFF, 0XCFFFE, 0XCFFFF, 0XDFFFE, 0XDFFFF, 0XEFFFE, 0XEFFFF, 0XFFFFE, 0XFFFFF, 0X10FFFE, 0x10FFFF -> false;
      default -> true;
    };
  }
  
}
