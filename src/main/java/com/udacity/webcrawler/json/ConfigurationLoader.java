package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class ConfigurationLoader {
  private final Path path;

  public ConfigurationLoader(Path path) { this.path = Objects.requireNonNull(path); }

  public CrawlerConfiguration load() {
    try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      return read(reader);
    } catch (IOException e) {
      throw new IllegalStateException("Could not load crawler configuration: " + path, e);
    }
  }

  public static CrawlerConfiguration read(Reader reader) {
    try {
      return new ObjectMapper().readValue(Objects.requireNonNull(reader), CrawlerConfiguration.Builder.class).build();
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid crawler configuration", e);
    }
  }
}