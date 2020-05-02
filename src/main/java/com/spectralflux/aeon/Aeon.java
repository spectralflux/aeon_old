package com.spectralflux.aeon;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.interpreter.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.spectralflux.aeon.syntax.statement.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aeon Language main class. Can run as an interpreter from the command line, or with a file.
 */
public class Aeon {

    private static final Logger logger = LoggerFactory.getLogger(Aeon.class);
    private static ErrorHandler errorHandler = new ErrorHandler();
    private static final Interpreter interpreter = new Interpreter(errorHandler);

    public static void main(String... args) {
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

        if (errorHandler.hadError()) {
            System.exit(65);
        }
        if (errorHandler.hadRuntimeError()) {
            System.exit(70);
        }

        run(new String(bytes, Charset.defaultCharset()));
        errorHandler.reset();
    }

    private static void runPrompt() throws IOException {

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (!Thread.currentThread().isInterrupted()) {
            System.out.print("> ");
            run(reader.readLine()  + "\n");
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(errorHandler, source);
        List<Token> tokens = scanner.scanTokens();

        tokens.forEach(token -> logger.debug(token.toString()));

        Parser parser = new Parser(errorHandler, tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (errorHandler.hadError()) {
            return;
        }

        Resolver resolver = new Resolver(errorHandler, interpreter);
        resolver.resolve(statements);

        // Stop if there was a resolution error.
        if (errorHandler.hadError()) {
            return;
        }

        TypeChecker typeChecker = new TypeChecker(errorHandler);
        typeChecker.checkTypes();

        // Stop if there was a type checking error.
        if (errorHandler.hadError()) {
            return;
        }

        interpreter.interpret(statements);
    }

}