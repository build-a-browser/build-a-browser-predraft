package net.buildabrowser.babbrowser.htmlparser.tokenize.imp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrieView;

public record MatchTrieImp(String base, Map<Integer, MatchTrie> extensions, boolean hasMatch) implements MatchTrie {

  @Override
  public MatchTrieView createView() {
    return new MatchTrieViewImp(this);
  }

  public static MatchTrie compile(List<String> options) {
    return compile_(options.stream().map(s -> s.toUpperCase()).collect(Collectors.toList()));
  }

  private static MatchTrie compile_(List<String> options) {
    if (options.isEmpty()) {
      throw new IllegalArgumentException("Options musn't be empty!");
    }
    int commonLength = findCommonLength(options, 0);
    
    String base = options.get(0).substring(0, commonLength);
    boolean hasMatch = options.contains(base);
    Map<Integer, MatchTrie> extensions = new HashMap<>();

    for (String option: options) {
      if (option.length() <= commonLength) continue;
      int startCodePoint = option.codePointAt(commonLength);
      extensions.computeIfAbsent(startCodePoint, _1 -> compile_(options.stream()
        .filter(s -> s.length() > commonLength)
        .filter(s -> s.codePointAt(commonLength) == startCodePoint)
        .map(s -> s.substring(commonLength + 1))
        .collect(Collectors.toList())));
    }

    return new MatchTrieImp(base, extensions, hasMatch);
  }

  private static int findCommonLength(List<String> options, int index) {
    if (options.get(0).length() <= index) return 0;
    int firstChar = options.get(0).codePointAt(index);
    for (String option: options) {
      if (!(
        option.length() > index
        && option.codePointAt(index) == firstChar
      )) {
        return 0;
      }
    }

    return 1 + findCommonLength(options, index + 1);
  }
  
}
