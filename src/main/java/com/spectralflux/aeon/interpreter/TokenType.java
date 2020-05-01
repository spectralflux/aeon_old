package com.spectralflux.aeon.interpreter;

public enum TokenType {
    // meaningful whitespace
    SPACE, TAB,

    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, NEWLINE, COLON, COMMA, DOT,
    MINUS, PLUS, SLASH, STAR, BANG,

    // One or two character tokens.
    EQUAL, GREATER, LESS,
    GREATER_EQUAL, LESS_EQUAL,
    BANG_EQUAL, EQUAL_EQUAL,

    // Literals.
    IDENTIFIER, STRING, INTEGER, FLOAT,

    //Keywords
    LET, FN, OR, AND, IF, ELSE, RETURN, TRUE, FALSE, FOR, IN, AS,

    // TODO identity keywords need special treatment, this is done poorly in python so do i need these?
    IS, NOT,

    EOF
}
