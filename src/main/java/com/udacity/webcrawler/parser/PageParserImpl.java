package com.udacity.webcrawler.parser;

import com.udacity.webcrawler.profiler.Profiled;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class PageParserImpl implements PageParser {
  private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}]+(?:['-][\\p{L}\\p{N}]+)*");
  private final String url;
  private final Duration timeout;
  private final List<Pattern> ignoredWords;

  public PageParserImpl(String url, Duration timeout, List<Pattern> ignoredWords) {
    this.url = Objects.requireNonNull(url);
    this.timeout = Objects.requireNonNull(timeout);
    this.ignoredWords = List.copyOf(ignoredWords);
  }

  @Override
  @Profiled
  public Result parse() {
    try {
      Document document = loadDocument();
      Result.Builder result = new Result.Builder();
      WORD.matcher(document.body() == null ? "" : document.body().text().toLowerCase(Locale.ROOT))
          .results()
          .map(match -> match.group())
          .filter(word -> ignoredWords.stream().noneMatch(pattern -> pattern.matcher(word).matches()))
          .forEach(result::addWord);
      for (Element element : document.select("a[href]")) {
        String link = element.absUrl("href");
        if (!link.isEmpty()) result.addLink(link);
      }
      return result.build();
    } catch (Exception e) {
      return new Result.Builder().build();
    }
  }

  private Document loadDocument() throws IOException {
    URI uri = URI.create(url);
    if ("file".equalsIgnoreCase(uri.getScheme())) {
      return Jsoup.parse(Files.readString(Path.of(uri), StandardCharsets.UTF_8), url);
    }
    int timeoutMillis = (int) Math.min(Integer.MAX_VALUE, Math.max(1, timeout.toMillis()));
    return Jsoup.connect(url).timeout(timeoutMillis).userAgent("UdacityWebCrawler/1.0").get();
  }
}