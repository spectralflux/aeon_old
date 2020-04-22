package com.spectralflux.aeon.interpreter;

public enum TokenType {
    // meaningful whitespace
    SPACE, TAB,

    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, NEWLINE, COLON, COMMA, DOT,
    MINUS, PLUS, SLASH, STAR,

    // Literals.
    IDENTIFIER, STRING, INTEGER, FLOAT,

    //Keywords
    LET, FN, IS, NOT, OR, AND, IF, ELSE, RETURN, TRUE, FALSE, FOR, IN, AS,

    EOF
}
