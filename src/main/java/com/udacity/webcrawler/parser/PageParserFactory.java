package com.udacity.webcrawler.parser;

public interface PageParserFactory {
  PageParser get(String url);
}