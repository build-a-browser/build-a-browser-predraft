package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.AfterAttributeValueQuotedState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.AttributeNameState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.AttributeValueDoubleQuotedState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.BeforeAttributeNameState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.BeforeAttributeValueState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.DataState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.EndTagOpenState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.RawTextEndTagNameState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.RawTextEndTagOpenState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.RawTextLessThanSignState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.RawTextState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.SelfClosingStartTagState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.TagNameState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.states.TagOpenState;

public final class TokenizeStates {
  
  private TokenizeStates() {}

  public static TokenizeState afterAttributeValueQuotedState = new AfterAttributeValueQuotedState();
  public static TokenizeState afterAttributeNameState = new AttributeNameState();
  public static TokenizeState attributeValueDoubleQuotedState = new AttributeValueDoubleQuotedState();
  public static TokenizeState beforeAttributeNameState = new BeforeAttributeNameState();
  public static TokenizeState beforeAttributeValueState = new BeforeAttributeValueState();
  public static TokenizeState dataState = new DataState();
  public static TokenizeState endTagOpenState = new EndTagOpenState();
  public static TokenizeState rawTextEndTagNameState = new RawTextEndTagNameState();
  public static TokenizeState rawTextEndTagOpenState = new RawTextEndTagOpenState();
  public static TokenizeState rawTextLessThanSignState = new RawTextLessThanSignState();
  public static TokenizeState rawTextState = new RawTextState();
  public static TokenizeState selfClosingStartTagState = new SelfClosingStartTagState();
  public static TokenizeState tagNameState = new TagNameState();
  public static TokenizeState tagOpenState = new TagOpenState();

}
