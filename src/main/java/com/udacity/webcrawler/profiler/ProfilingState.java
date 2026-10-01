package com.udacity.webcrawler.profiler;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

final class ProfilingState {
  private final Map<String, Duration> data = new ConcurrentHashMap<>();

  void record(Class<?> callingClass, Method method, Duration elapsed) {
    if (elapsed.isNegative()) throw new IllegalArgumentException("negative elapsed time");
    String key = callingClass.getName() + "#" + method.getName();
    data.compute(key, (ignored, current) -> current == null ? elapsed : current.plus(elapsed));
  }

  void write(Writer writer) throws IOException {
    for (String entry : data.entrySet().stream().sorted(Map.Entry.comparingByKey())
        .map(entry -> entry.getKey() + " took " + format(entry.getValue()) + System.lineSeparator())
        .collect(Collectors.toList())) writer.write(entry);
  }

  private static String format(Duration duration) {
    return String.format("%sm %ss %sms", duration.toMinutes(), duration.toSecondsPart(), duration.toMillisPart());
  }
}