package com.udacity.webcrawler.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CrawlerConfiguration {
  private final List<String> startPages;
  private final List<Pattern> ignoredUrls;
  private final List<Pattern> ignoredWords;
  private final int parallelism;
  private final String implementationOverride;
  private final int maxDepth;
  private final Duration timeout;
  private final int popularWordCount;
  private final String profileOutputPath;
  private final String resultPath;

  private CrawlerConfiguration(List<String> startPages, List<Pattern> ignoredUrls,
      List<Pattern> ignoredWords, int parallelism, String implementationOverride,
      int maxDepth, Duration timeout, int popularWordCount, String profileOutputPath,
      String resultPath) {
    this.startPages = List.copyOf(startPages);
    this.ignoredUrls = List.copyOf(ignoredUrls);
    this.ignoredWords = List.copyOf(ignoredWords);
    this.parallelism = parallelism;
    this.implementationOverride = implementationOverride;
    this.maxDepth = maxDepth;
    this.timeout = timeout;
    this.popularWordCount = popularWordCount;
    this.profileOutputPath = profileOutputPath;
    this.resultPath = resultPath;
  }

  public List<String> getStartPages() { return startPages; }
  public List<Pattern> getIgnoredUrls() { return ignoredUrls; }
  public List<Pattern> getIgnoredWords() { return ignoredWords; }
  public int getParallelism() { return parallelism; }
  public String getImplementationOverride() { return implementationOverride; }
  public int getMaxDepth() { return maxDepth; }
  public Duration getTimeout() { return timeout; }
  public int getPopularWordCount() { return popularWordCount; }
  public String getProfileOutputPath() { return profileOutputPath; }
  public String getResultPath() { return resultPath; }

  public static final class Builder {
    private final Set<String> startPages = new LinkedHashSet<>();
    private final Set<String> ignoredUrls = new LinkedHashSet<>();
    private final Set<String> ignoredWords = new LinkedHashSet<>();
    private int parallelism = -1;
    private String implementationOverride = "";
    private int maxDepth = 0;
    private int timeoutSeconds = 1;
    private int popularWordCount = 0;
    private String profileOutputPath = "";
    private String resultPath = "";

    @JsonProperty("startPages")
    public Builder addStartPages(String... pages) {
      for (String page : pages) startPages.add(Objects.requireNonNull(page));
      return this;
    }

    @JsonProperty("ignoredUrls")
    public Builder addIgnoredUrls(String... patterns) {
      for (String pattern : patterns) ignoredUrls.add(Objects.requireNonNull(pattern));
      return this;
    }

    @JsonProperty("ignoredWords")
    public Builder addIgnoredWords(String... patterns) {
      for (String pattern : patterns) ignoredWords.add(Objects.requireNonNull(pattern));
      return this;
    }

    @JsonProperty("parallelism")
    public Builder setParallelism(int parallelism) { this.parallelism = parallelism; return this; }

    @JsonProperty("implementationOverride")
    public Builder setImplementationOverride(String value) {
      implementationOverride = Objects.requireNonNull(value); return this;
    }

    @JsonProperty("maxDepth")
    public Builder setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; return this; }

    @JsonProperty("timeoutSeconds")
    public Builder setTimeoutSeconds(int seconds) { timeoutSeconds = seconds; return this; }

    @JsonProperty("popularWordCount")
    public Builder setPopularWordCount(int count) { popularWordCount = count; return this; }

    @JsonProperty("profileOutputPath")
    public Builder setProfileOutputPath(String path) {
      profileOutputPath = Objects.requireNonNull(path); return this;
    }

    @JsonProperty("resultPath")
    public Builder setResultPath(String path) {
      resultPath = Objects.requireNonNull(path); return this;
    }

    public CrawlerConfiguration build() {
      if (maxDepth < 0) throw new IllegalArgumentException("maxDepth cannot be negative");
      if (timeoutSeconds <= 0) throw new IllegalArgumentException("timeoutSeconds must be positive");
      if (popularWordCount < 0) throw new IllegalArgumentException("popularWordCount cannot be negative");
      if (parallelism == 0 || parallelism < -1) {
        throw new IllegalArgumentException("parallelism must be -1 or positive");
      }
      return new CrawlerConfiguration(
          startPages.stream().toList(),
          ignoredUrls.stream().map(Pattern::compile).collect(Collectors.toList()),
          ignoredWords.stream().map(Pattern::compile).collect(Collectors.toList()),
          parallelism, implementationOverride, maxDepth, Duration.ofSeconds(timeoutSeconds),
          popularWordCount, profileOutputPath, resultPath);
    }
  }
}