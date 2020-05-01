package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spectralflux.aeon.interpreter.TokenType.*;

/**
 * Could have called this the Lexer, but that's not technically what it's doing...
 */
public class Scanner {

  private final ErrorHandler errorHandler;
  private final String source;
  private final Map<String, TokenType> keywords;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  private boolean isTextStarted = false;

  public Scanner(ErrorHandler errorHandler, String source) {
    this.errorHandler = errorHandler;
    this.source = source;
    keywords = new HashMap<>() {{
      put("let", LET);
      put("fn", FN);
      put("true", TRUE);
      put("false", FALSE);
      put("if", IF);
      put("else", ELSE);
      put("return", RETURN);
      put("for", FOR);
      put("in", IN);
      put("as", AS);
    }};
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    // need one more newline if there wasn't one added at the end.
    if (tokens.get(tokens.size() - 1).getType() != NEWLINE) {
      tokens.add(new Token(NEWLINE, "\n", null, line));
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    // for any line, add indentation first
    while (!isTextStarted) {
      char indentChar = peek();
      if (indentChar != ' ' && indentChar != '\t') {
        isTextStarted = true;
        break;
      } else if (indentChar == ' ') {
        advance();
        addToken(SPACE);
        start = current;
      } else {
        advance();
        addToken(TAB);
        start = current;
      }
    }

    char c = advance();

    switch (c) {
      case ' ':
      case '\t':
        // we've already handled these at the start of the line, ignore all others
        break;
      case '#':
        if (match('*')) {
          // multiline comment
          while (peek() != '*' && peekNext() != '#' && !isAtEnd()) {
            if (peek() == '\n') {
              line++;
            }
            advance();
          }

          if (isAtEnd()) {
            errorHandler.error(line, "Unterminated multiline comment.");
          }

          // consume comment end
          advance();
          advance();

          if (peek() == '\n') {
            advance();
          } else {
            errorHandler.error(line, "Multiline comment must terminate with newline.");
          }

        } else {
          while (peek() != '\n' && !isAtEnd()) {
            advance();
          }

          if (peek() == '\n') {
            // consume this newline, otherwise parser will make this a blank expression.
            advance();
          }
        }
        break;
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case ':':
        addToken(COLON);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case '*':
        addToken(STAR);
        break;
      case '=':
        addToken(EQUAL);
        break;
      case '/':
        addToken(SLASH);
      case '\'':
        string();
        break;
      case '\n':
        incrementLine();
        addToken(NEWLINE);
        break;
      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          errorHandler.error(line, "Unexpected character.");
        }
    }
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }
    if (source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }
    return source.charAt(current);
  }

  /**
   * Could have made peek take an argument for lookahead size, but we don't want to encourage
   * arbitrary lookahead in lox so we'll stick with two methods here.
   */
  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }
    return source.charAt(current + 1);
  }

  private void incrementLine() {
    line++;
    isTextStarted = false;
  }

  private void string() {
    while (peek() != '\'' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    // Unterminated string.
    if (isAtEnd()) {
      errorHandler.error(line, "Unterminated string.");
      return;
    }

    // The closing '.
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private void number() {
    boolean isFloat = false;

    while (isDigit(peek())) {
      advance();
    }

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // we've found a float!
      isFloat = true;

      // Consume the "."
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    if (isFloat) {
      // I want higher precision floats than the Float type in Java provides, so making this a Double.
      addToken(FLOAT,
          Double.parseDouble(source.substring(start, current)));
    } else {
      addToken(INTEGER,
          Integer.parseInt(source.substring(start, current)));
    }
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }
    // See if the identifier is a reserved word.
    String text = source.substring(start, current);

    TokenType type = keywords.get(text);
    if (type == null) {
      type = IDENTIFIER;
    }
    addToken(type);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Java actually provides Character.isDigit(), but it allows a lot of wacky digit types
   * (Devanagari digits, fullwidth numbers, etc) so we'll make our own little one.
   */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private char advance() {
    current++;
    return source.charAt(current - 1);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }
}
