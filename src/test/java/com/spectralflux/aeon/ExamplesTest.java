package com.spectralflux.aeon;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.interpreter.Parser;
import com.spectralflux.aeon.interpreter.Scanner;
import com.spectralflux.aeon.interpreter.Token;
import com.spectralflux.aeon.syntax.statement.Stmt;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class ExamplesTest {

  private ErrorHandler errorHandler;
  private List<String> exampleFiles = new ArrayList<>() {{
    add("assignment.aeon");
  }};

  @Test
  void runExamples() {
    ClassLoader classLoader = getClass().getClassLoader();

    exampleFiles.forEach(resourceName -> {

      errorHandler = new ErrorHandler();
      try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {

        String source = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        System.out.println("-------------------------------");
        System.out.println(source);
        System.out.println("-------------------------------");

        Scanner scanner = new Scanner(errorHandler, source);
        List<Token> tokens = scanner.scanTokens();

        tokens.forEach(System.out::println);
        System.out.println("-------------------------------");

        Parser parser = new Parser(errorHandler, tokens);
        List<Stmt> statements = parser.parse();

        statements.forEach(System.out::println);
        System.out.println("-------------------------------");

        assertFalse(errorHandler.hadError());
        assertFalse(errorHandler.hadRuntimeError());

      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

}
