package com.udacity.webcrawler.profiler;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public interface Profiler {
  <T> T wrap(Class<T> klass, T delegate);
  void writeData(Path path) throws IOException;
  void writeData(Writer writer) throws IOException;
}