package com.udacity.webcrawler.parser;

import com.udacity.webcrawler.profiler.Profiler;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class PageParserFactoryImpl implements PageParserFactory {
  private final Duration timeout;
  private final List<Pattern> ignoredWords;
  private final Profiler profiler;

  public PageParserFactoryImpl(Duration timeout, List<Pattern> ignoredWords, Profiler profiler) {
    this.timeout = Objects.requireNonNull(timeout);
    this.ignoredWords = List.copyOf(ignoredWords);
    this.profiler = Objects.requireNonNull(profiler);
  }

  @Override
  public PageParser get(String url) {
    return profiler.wrap(PageParser.class, new PageParserImpl(url, timeout, ignoredWords));
  }
}