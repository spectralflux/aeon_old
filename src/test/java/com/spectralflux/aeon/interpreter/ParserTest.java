package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.interpreter.Parser;
import com.spectralflux.aeon.interpreter.Token;
import com.spectralflux.aeon.syntax.expression.Literal;
import com.spectralflux.aeon.syntax.statement.Let;
import com.spectralflux.aeon.syntax.statement.Stmt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.spectralflux.aeon.interpreter.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

  private ErrorHandler errorHandler;

  @BeforeEach
  void setup() {
    errorHandler = new ErrorHandler();
  }

  @Test
  void testParseAssignment() {
    String varName = "x";
    String varValue = "hello";

    List<Token> tokens = new ArrayList<>() {{
      add(new Token(LET, "let", null, 1));
      add(new Token(IDENTIFIER, varName, null, 1));
      add(new Token(EQUAL, "=", null, 1));
      add(new Token(STRING, String.format("'%s'", varName), varValue, 1));
      add(new Token(NEWLINE, "\n", null, 1));
      add(new Token(EOF, "", null, 1));
    }};

    Parser parser = new Parser(errorHandler, tokens);

    List<Stmt> statements = parser.parse();

    assertFalse(statements.isEmpty());
    assertEquals(1, statements.size());
    statements.forEach(statement -> {
      assertTrue(statement instanceof Let);
      Let letStatement = (Let) statement;
      assertEquals(varName, letStatement.getName().getLexeme());
      assertEquals(varValue, ((Literal) letStatement.getInitializer()).getValue());
    });
  }

}