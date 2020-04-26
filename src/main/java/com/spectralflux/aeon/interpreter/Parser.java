package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.error.ParseError;
import com.spectralflux.aeon.syntax.expression.*;
import com.spectralflux.aeon.syntax.statement.Expression;
import com.spectralflux.aeon.syntax.statement.Function;
import com.spectralflux.aeon.syntax.statement.Let;
import com.spectralflux.aeon.syntax.statement.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.spectralflux.aeon.interpreter.TokenType.*;

public class Parser {

  private static final Logger logger = LoggerFactory.getLogger(Parser.class);

  private final List<Token> tokens;
  private final ErrorHandler errorHandler;
  private int current;

  private int previousIndent;
  private int indent;

  public Parser(ErrorHandler errorHandler, List<Token> tokens) {
    this.errorHandler = errorHandler;
    this.tokens = tokens;
    this.current = 0;

    this.indent = 0;
    this.previousIndent = 0;
  }

  public List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();

    while (!isAtEnd()) {
      //consumeWhitespace();
      statements.add(declaration());
    }

    return statements;
  }

  private void consumeWhitespace() {
    while (check(SPACE) || check(TAB)) {
      if (check(SPACE)) {
        consume(SPACE, "Expect space character.");
      } else {
        indent += 4; // TODO check how python does this...
        consume(TAB, "Expect tab character.");
      }
    }

    logger.debug(String.format("indent=%s, previousIndent=%s ", indent, previousIndent));
  }

  private Stmt declaration() {
    try {

      if (match(FN)) {
        return function("function");
      }

      if (match(LET)) {
        return letDeclaration();
      }

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt letDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(NEWLINE, "Expect newline after variable declaration.");
    return new Let(name, initializer);
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

    // while we are still indented, we are still in the block
    while (previousIndent <= indent) {
      statements.add(declaration());
    }

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
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Variable) {
        Token name = ((Variable) expr).getName();
        return new Assign(name, value);
      } else if (expr instanceof Get) {
        Get get = (Get) expr;
        return new Set(get.getObject(), get.getName(), value);
      }

      throw error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr equality() {
    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr comparison() {
    Expr expr = addition();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = addition();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr addition() {
    Expr expr = multiplication();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = multiplication();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr multiplication() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Unary(operator, right);
    }

    return call();
  }

  private Expr call() {
    Expr expr = primary();

    while (true) {
      if (match(LEFT_PAREN)) {
        expr = finishCall(expr);
      } else if (match(DOT)) {
        Token name = consume(IDENTIFIER,
            "Expect property name after '.'.");
        expr = new Get(expr, name);
      } else {
        break;
      }
    }

    return expr;
  }

  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        // java supports a max of 255 arguments
        if (arguments.size() >= 255) {
          error(peek(), "Cannot have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }

    Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");

    return new Call(callee, paren, arguments);
  }

  private Expr primary() {
    if (match(FALSE)) {
      return new Literal(false);
    }
    if (match(TRUE)) {
      return new Literal(true);
    }

    if (match(INTEGER, FLOAT, STRING)) {
      return new Literal(previous().getLiteral());
    }

    if (match(IDENTIFIER)) {
      return new Variable(previous());
    }

    // TODO "this" and "super" need to be defined here when classes are added.

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
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
      Token next = advance();

      if (type == NEWLINE) {
        resetLine();
      }

      return next;
    }

    throw error(peek(), message);
  }

  private void resetLine() {
    previousIndent = indent;
    indent = 0;

    while (check(SPACE) || check(TAB)) {
      if (check(SPACE)) {
        consume(SPACE, "Space expected.");
        indent += 1;
      } else {
        consume(TAB, "Tab expected.");
        indent += 4;
      }
    }

    logger.debug(String.format("indent=%s, previousIndent=%s ", indent, previousIndent));
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
