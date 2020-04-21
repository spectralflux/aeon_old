package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.error.RuntimeError;
import com.spectralflux.aeon.syntax.statement.Stmt;

import java.util.List;

public class Interpreter {

    private final ErrorHandler errorHandler;

    public Interpreter(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                //execute(statement);
            }
        } catch (RuntimeError error) {
            errorHandler.runtimeError(error);
        }
    }
}
