package com.spectralflux.aeon.scan;

public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN,

    // Literals.
    IDENTIFIER, STRING, INTEGER, FLOAT,

    //Keywords
    LET, NEWLINE,

    EOF
}
