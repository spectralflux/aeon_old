package com.spectralflux.aeon.exception;

import com.spectralflux.aeon.scan.Token;

public class RuntimeError extends RuntimeException {

  private final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}