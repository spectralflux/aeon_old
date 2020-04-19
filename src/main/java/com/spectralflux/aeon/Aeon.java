package com.spectralflux.aeon;

import com.spectralflux.aeon.exception.RuntimeError;
import com.spectralflux.aeon.scan.Token;
import com.spectralflux.aeon.scan.TokenType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aeon Language main class. Can run as an interpreter from the command line, or with a file.
 */
public class Aeon {

  static Logger logger = LoggerFactory.getLogger(Aeon.class);

  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String... args) {
    logger.info("Aeon Language");
    Thread runThread = new Thread(() -> {
      try {
        if (args.length > 1) {
          System.out.println("Usage: aeon [script]");
          System.exit(64);
        } else if (args.length == 1) {
          runFile(args[0]);
        } else {
          runPrompt();
          System.exit(0);
        }
      } catch (IOException e) {
        logger.error(Arrays.toString(e.getStackTrace()));
        System.exit(1);
      }
    });

    Runtime.getRuntime().addShutdownHook(new Thread(runThread::interrupt));

    runThread.start();
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    if (hadError) {
      System.exit(65);
    }
    if (hadRuntimeError) {
      System.exit(70);
    }

    run(new String(bytes, Charset.defaultCharset()));
    hadError = false;
  }

  private static void runPrompt() throws IOException {

    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    while (!Thread.currentThread().isInterrupted()) {
      System.out.print("> ");
      run(reader.readLine());
    }
  }

  private static void run(String source) {
    /*Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    // Stop if there was a syntax error.
    if (hadError) {
      return;
    }

    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);

    // Stop if there was a resolution error.
    if (hadError) {
      return;
    }

    interpreter.interpret(statements);*/
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.getType() == TokenType.EOF) {
      report(token.getLine(), " at end", message);
    } else {
      report(token.getLine(), " at '" + token.getLexeme() + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.getToken().getLine() + "]");
    hadRuntimeError = true;
  }
}