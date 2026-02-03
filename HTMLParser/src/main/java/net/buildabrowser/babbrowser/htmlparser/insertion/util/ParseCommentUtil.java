package net.buildabrowser.babbrowser.htmlparser.insertion.util;

import net.buildabrowser.babbrowser.dom.mutable.MutableComment;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;

public final class ParseCommentUtil {
  
  private ParseCommentUtil() {}

  public static void insertAComment(ParseContext parseContext, CommentToken commentToken) {
    insertAComment(parseContext, commentToken, null);
  }

  public static void insertAComment(ParseContext parseContext, CommentToken commentToken, MutableNode targetOverride) {
    String data = commentToken.data();
    MutableNode adjustedInsertionLocation = ParseElementUtil.appropriatePlaceForInsertingANode(parseContext, targetOverride);
    MutableComment comment = MutableComment.create(data);
    adjustedInsertionLocation.appendChild(comment);
  }

}
