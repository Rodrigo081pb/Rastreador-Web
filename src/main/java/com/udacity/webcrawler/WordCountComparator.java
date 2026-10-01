package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.Map;

public final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {
  @Override
  public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
    int countOrder = Integer.compare(right.getValue(), left.getValue());
    return countOrder != 0 ? countOrder : left.getKey().compareTo(right.getKey());
  }
}