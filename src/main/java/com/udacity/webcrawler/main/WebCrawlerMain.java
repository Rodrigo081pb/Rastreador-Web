package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class WebCrawlerMain {
  private final CrawlerConfiguration config;
  private WebCrawlerMain(CrawlerConfiguration config) { this.config = config; }

  private void run() throws Exception {
    var injector = Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule());
    WebCrawler crawler = injector.getInstance(WebCrawler.class);
    Profiler profiler = injector.getInstance(Profiler.class);
    var result = crawler.crawl(config.getStartPages());

    if (config.getResultPath().isEmpty()) {
      Writer writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
      new CrawlResultWriter(result).write(writer);
      writer.write(System.lineSeparator());
      writer.flush();
    } else {
      new CrawlResultWriter(result).write(Path.of(config.getResultPath()));
    }

    if (config.getProfileOutputPath().isEmpty()) {
      Writer writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
      profiler.writeData(writer);
      writer.flush();
    } else {
      profiler.writeData(Path.of(config.getProfileOutputPath()));
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Usage: WebCrawlerMain <config-path>");
      return;
    }
    new WebCrawlerMain(new ConfigurationLoader(Path.of(args[0])).load()).run();
  }
}