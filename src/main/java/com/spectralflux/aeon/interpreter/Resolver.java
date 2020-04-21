package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.syntax.statement.Stmt;

import java.util.List;

public class Resolver {

    private final Interpreter interpreter;
    private final ErrorHandler errorHandler;

    public Resolver(ErrorHandler errorHandler, Interpreter interpreter) {
        this.errorHandler = errorHandler;
        this.interpreter = interpreter;
    }

    public void resolve(List<Stmt> statements) {
        // TODO implement me!
    }
}
