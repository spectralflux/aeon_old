package com.spectralflux.aeon.scan;

import com.spectralflux.aeon.error.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.spectralflux.aeon.scan.TokenType.*;

public class Scanner {

    private final ErrorHandler errorHandler;
    private final String source;
    private final Map<String, TokenType> keywords;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(ErrorHandler errorHandler, String source) {
        this.errorHandler = errorHandler;
        this.source = source;
        keywords = new HashMap<>() {{
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
            case '\n':
                line++;
                addToken(NEWLINE);
                break;
            default:
                if (isDigit(c)) {
                    // TODO add number handling
                    //number();
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

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
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
