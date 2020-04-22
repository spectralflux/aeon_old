package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.error.ParseError;
import com.spectralflux.aeon.syntax.expression.Expr;
import com.spectralflux.aeon.syntax.statement.Expression;
import com.spectralflux.aeon.syntax.statement.Function;
import com.spectralflux.aeon.syntax.statement.Stmt;

import java.util.ArrayList;
import java.util.List;

import static com.spectralflux.aeon.interpreter.TokenType.*;

public class Parser {

    private final List<Token> tokens;
    private final ErrorHandler errorHandler;
    private int current;

    public Parser(ErrorHandler errorHandler, List<Token> tokens) {
        this.errorHandler = errorHandler;
        this.tokens = tokens;
        this.current = 0;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {

            if (match(FN)) {
                return function("function");
            }

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    throw error(peek(), "Cannot have more than 255 parameters.");
                }

                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");

        consume(COLON, "Expect ':' before " + kind + " body.");

        consume(NEWLINE, "Expect new line before " + kind + " body.");

        List<Stmt> body = block();
        return new Function(name, parameters, body);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        // TODO add logic to add declarations to statements list

        // TODO add logic to detect de-indenting to signify end of block.

        return statements;
    }

    private Stmt statement() {
        // TODO add if statements / case block for different statements

        return expressionStatement();
    }

    private Stmt expressionStatement() {
        Expr expr = expression();

        consume(NEWLINE, "Expect new line after expression.");

        return new Expression(expr);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        // TODO need blocks for picking expressions here, then return the correct type of expression.
        return new Expr() {
        };
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean isAtEnd() {
        return peek().getType() == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == NEWLINE) {
                return;
            }

            switch (peek().getType()) {
                case FN:
                    return;
            }

            advance();
        }
    }

    private ParseError error(Token token, String message) {
        errorHandler.error(token, message);
        return new ParseError();
    }
}
