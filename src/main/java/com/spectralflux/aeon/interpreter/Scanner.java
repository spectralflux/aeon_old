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
    private int indent = 0;
    private boolean isTextStarted = false;

    public Scanner(ErrorHandler errorHandler, String source) {
        this.errorHandler = errorHandler;
        this.source = source;
        keywords = new HashMap<>() {{
            // TODO add all keywords
            put("let", LET);
        }};
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case ' ':
                addToken(SPACE);
                break;
            case '\t':
                addToken(TAB);
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
            case '/':
                addToken(SLASH);
            case '\'':
                string();
                break;
            case '\n':
                line++;
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
        if(type == NEWLINE) {
            resetIndent();
        } else if(type != SPACE && type != TAB) {
            isTextStarted = true;
        }

        if((type == SPACE || type == TAB) && !isTextStarted) {
            if (type == SPACE) {
                indent += 1;
            } else {
                indent += 4; // TODO check this, see how python does it
            }
        }

        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void resetIndent() {
        indent = 0;
        isTextStarted = false;
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
