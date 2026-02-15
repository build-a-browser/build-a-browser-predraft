package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.util.Map;

import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrieView;

public class MatchTrieViewImp implements MatchTrieView {

  private static final MatchTrieImp EMPTY_TRIE = new MatchTrieImp("", Map.of(), false);

  private MatchTrieImp currentTrie;
  private int currentIndex = 0;

  public MatchTrieViewImp(MatchTrieImp currentTrie) {
    this.currentTrie = currentTrie;
  }

  @Override
  public void narrow(int ch) {
    if (currentIndex == currentTrie.base().length()) {
      MatchTrieImp newTrie = (MatchTrieImp) currentTrie.extensions().get(ch);
      this.currentTrie = newTrie == null ? EMPTY_TRIE : newTrie;
    } else if (currentTrie.base().codePointAt(currentIndex) == ch) {
      currentIndex++;
    } else {
      this.currentTrie = EMPTY_TRIE;
    }
  }

  @Override
  public boolean hasMatch() {
    return currentIndex == currentTrie.base().length() && currentTrie.hasMatch();
  }

  @Override
  public boolean continues() {
    return
      currentIndex <= currentTrie.base().length()
      && (
        !currentTrie.extensions().isEmpty()
        || currentTrie.hasMatch());
  }

}
