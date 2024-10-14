package io.mpolivaha.maven.plugin.editorconfig.processing;

public enum IndentationStyle {

  TAB('\t'),
  SPACE(' ');

  private final char encoding;

  IndentationStyle(char c) {
    this.encoding = c;
  }

  public char getEncoding() {
    return encoding;
  }
}
