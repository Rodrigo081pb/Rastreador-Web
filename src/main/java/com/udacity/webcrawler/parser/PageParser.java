package com.udacity.webcrawler.parser;

import com.udacity.webcrawler.profiler.Profiled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PageParser {
  @Profiled
  Result parse();

  final class Result {
    private final Map<String, Integer> wordCounts;
    private final List<String> links;

    private Result(Map<String, Integer> wordCounts, List<String> links) {
      this.wordCounts = Collections.unmodifiableMap(wordCounts);
      this.links = Collections.unmodifiableList(links);
    }

    public Map<String, Integer> getWordCounts() { return wordCounts; }
    public List<String> getLinks() { return links; }

    public static final class Builder {
      private final Map<String, Integer> wordCounts = new java.util.HashMap<>();
      private final List<String> links = new ArrayList<>();

      public Builder addWord(String word) { wordCounts.merge(word, 1, Integer::sum); return this; }
      public Builder addWordCount(String word, int count) { wordCounts.merge(word, count, Integer::sum); return this; }
      public Builder addLink(String link) { links.add(link); return this; }
      public Result build() {
        return new Result(new java.util.HashMap<>(wordCounts), links.stream().collect(Collectors.toList()));
      }
    }
  }
}