package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

public final class ProfilingMethodInterceptor implements InvocationHandler {
  private final Clock clock;
  private final Object delegate;
  private final ProfilingState state;

  public ProfilingMethodInterceptor(Clock clock, Object delegate, ProfilingState state) {
    this.clock = Objects.requireNonNull(clock);
    this.delegate = Objects.requireNonNull(delegate);
    this.state = Objects.requireNonNull(state);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getDeclaringClass() == Object.class && method.getName().equals("equals")) {
      return invokeOriginal(method, args);
    }
    if (!method.isAnnotationPresent(Profiled.class)) return invokeOriginal(method, args);
    long start = clock.millis();
    try {
      return invokeOriginal(method, args);
    } finally {
      state.record(delegate.getClass(), method, Duration.ofMillis(clock.millis() - start));
    }
  }

  private Object invokeOriginal(Method method, Object[] args) throws Throwable {
    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("Could not access intercepted method: " + method, e);
    }
  }
}