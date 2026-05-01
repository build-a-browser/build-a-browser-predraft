package net.buildabrowser.babbrowser.htmlparser.tokenize;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.MatchTrieImp;

public interface MatchTrie {

  MatchTrieView createView();

  static MatchTrie compile(List<String> options) {
    return MatchTrieImp.compile(options);
  }

}
