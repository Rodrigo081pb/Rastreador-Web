package com.udacity.webcrawler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.parser.PageParserFactory;
import com.udacity.webcrawler.parser.PageParserFactoryImpl;
import com.udacity.webcrawler.profiler.Profiler;

import javax.inject.Singleton;
import java.time.Clock;

public final class WebCrawlerModule extends AbstractModule {
  private final CrawlerConfiguration config;

  public WebCrawlerModule(CrawlerConfiguration config) { this.config = config; }

  @Override
  protected void configure() {
    bind(CrawlerConfiguration.class).toInstance(config);
    bind(Clock.class).toInstance(Clock.systemUTC());
  }

  @Provides
  @Singleton
  PageParserFactory providePageParserFactory(Profiler profiler) {
    return new PageParserFactoryImpl(config.getTimeout(), config.getIgnoredWords(), profiler);
  }

  @Provides
  @Singleton
  WebCrawler provideWebCrawler(Clock clock, PageParserFactory parserFactory, Profiler profiler) {
    int parallelism = config.getParallelism() < 0
        ? Runtime.getRuntime().availableProcessors() : config.getParallelism();
    WebCrawler crawler;
    if (config.getImplementationOverride().isEmpty()) {
      crawler = parallelism <= 1
          ? new SequentialWebCrawler(clock, config.getTimeout(), config.getPopularWordCount(),
              parserFactory, config.getMaxDepth(), config.getIgnoredUrls())
          : new ParallelWebCrawler(clock, config.getTimeout(), config.getPopularWordCount(),
              parserFactory, config.getMaxDepth(), parallelism, config.getIgnoredUrls());
    } else if (config.getImplementationOverride().equals(SequentialWebCrawler.class.getName())) {
      crawler = new SequentialWebCrawler(clock, config.getTimeout(), config.getPopularWordCount(),
          parserFactory, config.getMaxDepth(), config.getIgnoredUrls());
    } else if (config.getImplementationOverride().equals(ParallelWebCrawler.class.getName())) {
      crawler = new ParallelWebCrawler(clock, config.getTimeout(), config.getPopularWordCount(),
          parserFactory, config.getMaxDepth(), parallelism, config.getIgnoredUrls());
    } else {
      throw new IllegalArgumentException("Unknown crawler implementation: " + config.getImplementationOverride());
    }
    return profiler.wrap(WebCrawler.class, crawler);
  }
}