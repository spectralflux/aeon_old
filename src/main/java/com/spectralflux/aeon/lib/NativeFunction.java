package com.spectralflux.aeon.lib;

public interface NativeFunction {

  default String getStringRepresentation() {
    return "<native fn>";
  }

}
