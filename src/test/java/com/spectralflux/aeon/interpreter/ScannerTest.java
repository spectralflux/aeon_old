package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.interpreter.Scanner;
import com.spectralflux.aeon.interpreter.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ScannerTest {

  private ErrorHandler errorHandler;

  @BeforeEach
  void setup() {
    errorHandler = new ErrorHandler();
  }

  @Test
  void testHelloWorld() {
    String source = "print('Hello, World!')\n";
    Scanner scanner = new Scanner(errorHandler, source);

    List<Token> tokens = scanner.scanTokens();

    // just want to see what's in there for now
    tokens.forEach(System.out::println);

    assertFalse(tokens.isEmpty());
    assertFalse(errorHandler.hadError());
  }

}