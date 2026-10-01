package com.udacity.webcrawler.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CrawlResult {
  private final Map<String, Integer> wordCounts;
  private final int urlsVisited;

  private CrawlResult(Map<String, Integer> wordCounts, int urlsVisited) {
    this.wordCounts = Collections.unmodifiableMap(new LinkedHashMap<>(wordCounts));
    this.urlsVisited = urlsVisited;
  }

  public Map<String, Integer> getWordCounts() { return wordCounts; }
  public int getUrlsVisited() { return urlsVisited; }

  public static final class Builder {
    private Map<String, Integer> wordFrequencies = new LinkedHashMap<>();
    private int pageCount;

    public Builder setWordCounts(Map<String, Integer> wordCounts) {
      wordFrequencies = Objects.requireNonNull(wordCounts); return this;
    }

    public Builder setUrlsVisited(int pageCount) { this.pageCount = pageCount; return this; }
    public CrawlResult build() { return new CrawlResult(wordFrequencies, pageCount); }
  }
}