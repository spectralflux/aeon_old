package com.spectralflux.aeon.error;

import com.spectralflux.aeon.interpreter.Token;
import com.spectralflux.aeon.interpreter.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {

    static Logger logger = LoggerFactory.getLogger(ErrorHandler.class);


    private boolean hadError = false;
    private boolean hadRuntimeError = false;

    public ErrorHandler() {

    }

    public void error(int line, String message) {
        report(line, "", message);
    }

    private void report(int line, String where, String message) {
        logger.error(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLine(), " at end", message);
        } else {
            report(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
    }

    public void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.getToken().getLine() + "]");
        hadRuntimeError = true;
    }

    public boolean hadError() {
        return hadError;
    }

    public boolean hadRuntimeError() {
        return hadRuntimeError;
    }

    public void reset() {
        hadError = false;
        hadRuntimeError = false;
    }
}
