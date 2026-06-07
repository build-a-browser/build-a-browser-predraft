package net.buildabrowser.babbrowser.cssbase.property.content;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public interface ContentValue extends CSSValue {
  
  static final ContentValue NORMAL = new ContentValue() {};
  
  static record StringContentValue(String content) implements ContentValue {

    public static StringContentValue create(String content) {
      return new StringContentValue(content);
    }

  }

}
