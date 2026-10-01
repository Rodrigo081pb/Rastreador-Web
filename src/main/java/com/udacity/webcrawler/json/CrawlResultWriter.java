package com.udacity.webcrawler.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class CrawlResultWriter {
  private final CrawlResult result;

  public CrawlResultWriter(CrawlResult result) { this.result = Objects.requireNonNull(result); }

  public void write(Path path) {
    Objects.requireNonNull(path);
    try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      write(writer);
    } catch (IOException e) {
      throw new IllegalStateException("Could not write crawl result: " + path, e);
    }
  }

  public void write(Writer writer) {
    Objects.requireNonNull(writer);
    try {
      ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
      mapper.getFactory().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
      mapper.writeValue(writer, result);
    } catch (IOException e) {
      throw new IllegalStateException("Could not write crawl result", e);
    }
  }
}