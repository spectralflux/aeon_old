package com.spectralflux.aeon.interpreter;

public class Token {

  private final TokenType type;
  private final String lexeme;
  private final Object literal;
  private final int line;

  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public int getLine() {
    return line;
  }

  public TokenType getType() {
    return type;
  }

  public String getLexeme() {
    return lexeme;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }

}
