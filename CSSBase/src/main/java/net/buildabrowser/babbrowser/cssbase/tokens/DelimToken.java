package net.buildabrowser.babbrowser.cssbase.tokens;

public record DelimToken(int ch) implements Token {

  private static final DelimToken[] tokenCache = new DelimToken[128];
  
  public static DelimToken create(int ch) {
    if (ch < 128) {
      if (tokenCache[ch] == null) {
        tokenCache[ch] = new DelimToken(ch);
      }
      return tokenCache[ch];
    }
    
    return new DelimToken(ch);
  }

}
