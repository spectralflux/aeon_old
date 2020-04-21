package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.syntax.statement.Stmt;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private final ErrorHandler errorHandler;

    public Parser(ErrorHandler errorHandler, List<Token> tokens) {
        this.errorHandler = errorHandler;
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        return statements;
    }
}
