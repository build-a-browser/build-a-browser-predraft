package net.buildabrowser.babbrowser.cssbase.property.content;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public interface ContentValue extends CSSValue {
  
  static final ContentValue NORMAL = new ContentValue() {
    @Override
    public String serialize() {
      return "normal";
    }
  };
  
  static record StringContentValue(String content) implements ContentValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeString(content);
    }

    public static StringContentValue create(String content) {
      return new StringContentValue(content);
    }

  }

}
