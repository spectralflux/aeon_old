package com.spectralflux.aeon.interpreter;

import static org.junit.jupiter.api.Assertions.*;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.syntax.expression.Call;
import com.spectralflux.aeon.syntax.expression.Expr;
import com.spectralflux.aeon.syntax.expression.Literal;
import com.spectralflux.aeon.syntax.expression.Variable;
import com.spectralflux.aeon.syntax.statement.Expression;
import com.spectralflux.aeon.syntax.statement.Stmt;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterpreterTest {

  @Test
  void testPrintFunction() {
    ErrorHandler errorHandler = new ErrorHandler();
    Interpreter interpreter = new Interpreter(errorHandler);

    List<Stmt> statements = new ArrayList<>();
    List<Expr> args = new ArrayList<>();
    args.add(new Literal("Hello, World!"));
    Variable callee = new Variable(new Token(TokenType.IDENTIFIER, "print", "print", 1));
    Call printCall = new Call(callee, new Token(TokenType.LEFT_PAREN, "(", null, 1), args);
    statements.add(new Expression(printCall));

    interpreter.interpret(statements);

    assertFalse(errorHandler.hadRuntimeError());
    assertFalse(errorHandler.hadError());
  }

}