package com.udacity.webcrawler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class WordCounts {
  private WordCounts() {}

  public static Map<String, Integer> sort(Map<String, Integer> counts, int popularWordCount) {
    return counts.entrySet().stream()
        .sorted(new WordCountComparator())
        .limit(popularWordCount)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
            (left, right) -> left, LinkedHashMap::new));
  }
}