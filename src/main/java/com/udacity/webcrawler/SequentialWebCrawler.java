package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class SequentialWebCrawler implements WebCrawler {
  private final Clock clock;
  private final Duration timeout;
  private final int popularWordCount;
  private final PageParserFactory parserFactory;
  private final int maxDepth;
  private final List<Pattern> ignoredUrls;

  public SequentialWebCrawler(Clock clock, Duration timeout, int popularWordCount,
      PageParserFactory parserFactory, int maxDepth, List<Pattern> ignoredUrls) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.parserFactory = parserFactory;
    this.maxDepth = maxDepth;
    this.ignoredUrls = ignoredUrls;
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    Set<String> visited = new HashSet<>();
    ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
    long deadline = System.nanoTime() + timeout.toNanos();
    for (String url : startingUrls) crawlInternal(url, 0, deadline, visited, counts);
    return new CrawlResult.Builder().setWordCounts(WordCounts.sort(counts, popularWordCount))
        .setUrlsVisited(visited.size()).build();
  }

  private void crawlInternal(String url, int depth, long deadline, Set<String> visited,
      ConcurrentHashMap<String, Integer> counts) {
    if (depth >= maxDepth || System.nanoTime() >= deadline || ignored(url)) return;
    if (!visited.add(url)) return;
    PageParser.Result result = parserFactory.get(url).parse();
    result.getWordCounts().forEach((word, count) -> counts.merge(word, count, Integer::sum));
    if (System.nanoTime() >= deadline) return;
    for (String link : result.getLinks()) crawlInternal(link, depth + 1, deadline, visited, counts);
  }

  private boolean ignored(String url) { return ignoredUrls.stream().anyMatch(pattern -> pattern.matcher(url).matches()); }
}