package com.spectralflux.aeon.interpreter;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, NEWLINE, COLON, COMMA,

    // Literals.
    IDENTIFIER, STRING, INTEGER, FLOAT,

    //Keywords
    LET, FN,

    EOF
}
