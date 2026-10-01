package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public final class ParallelWebCrawler implements WebCrawler {
  private final Clock clock;
  private final Duration timeout;
  private final int popularWordCount;
  private final PageParserFactory parserFactory;
  private final int maxDepth;
  private final int targetParallelism;
  private final List<Pattern> ignoredUrls;

  public ParallelWebCrawler(Clock clock, Duration timeout, int popularWordCount,
      PageParserFactory parserFactory, int maxDepth, int targetParallelism,
      List<Pattern> ignoredUrls) {
    this.clock = clock;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.parserFactory = parserFactory;
    this.maxDepth = maxDepth;
    this.targetParallelism = Math.max(2, targetParallelism);
    this.ignoredUrls = List.copyOf(ignoredUrls);
  }

  @Override
  public CrawlResult crawl(List<String> startingUrls) {
    long deadline = System.nanoTime() + timeout.toNanos();
    ConcurrentHashMap.KeySetView<String, Boolean> visited = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
    ForkJoinPool pool = new ForkJoinPool(targetParallelism);
    try {
      for (String url : startingUrls) {
        if (System.nanoTime() >= deadline) break;
        pool.invoke(new CrawlTask(url, 0, deadline, visited, counts));
      }
    } finally {
      pool.shutdown();
    }
    return new CrawlResult.Builder().setWordCounts(WordCounts.sort(counts, popularWordCount))
        .setUrlsVisited(visited.size()).build();
  }

  @Override
  public int getMaxParallelism() { return targetParallelism; }

  private final class CrawlTask extends RecursiveAction {
    private final String url;
    private final int depth;
    private final long deadline;
    private final ConcurrentHashMap.KeySetView<String, Boolean> visited;
    private final ConcurrentHashMap<String, Integer> counts;

    private CrawlTask(String url, int depth, long deadline,
        ConcurrentHashMap.KeySetView<String, Boolean> visited,
        ConcurrentHashMap<String, Integer> counts) {
      this.url = url; this.depth = depth; this.deadline = deadline;
      this.visited = visited; this.counts = counts;
    }

    @Override
    protected void compute() {
      if (depth >= maxDepth || System.nanoTime() >= deadline || ignored(url) || !visited.add(url)) return;
      PageParser.Result result = parserFactory.get(url).parse();
      result.getWordCounts().forEach((word, count) -> counts.merge(word, count, Integer::sum));
      if (depth + 1 >= maxDepth || System.nanoTime() >= deadline) return;
      List<CrawlTask> children = result.getLinks().stream()
          .filter(link -> !ignored(link))
          .map(link -> new CrawlTask(link, depth + 1, deadline, visited, counts))
          .toList();
      invokeAll(children);
    }
  }

  private boolean ignored(String url) { return ignoredUrls.stream().anyMatch(pattern -> pattern.matcher(url).matches()); }
}