package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.Comment;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;

public final class ParseCommentUtil {
  
  private ParseCommentUtil() {}

  public static void insertAComment(ParseContext parseContext, CommentToken commentToken) {
    insertAComment(parseContext, commentToken, null);
  }

  public static void insertAComment(ParseContext parseContext, CommentToken commentToken, Node targetOverride) {
    String data = commentToken.data();
    Node adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, targetOverride);
    Comment comment = Comment.create(data);
    adjustedInsertionLocation.appendChild(comment);
  }

}
