package com.spectralflux.aeon.callable;

/**
 * Implementing return statements as a always-caught runtime exception is a bit gross, but it
 * works!
 */
public class Return extends RuntimeException {

  private final Object value;

  Return(Object value) {
    super(null, null, false, false);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }
}
