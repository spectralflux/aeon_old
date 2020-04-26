package com.spectralflux.aeon.callable;

import com.spectralflux.aeon.interpreter.Interpreter;
import java.util.List;

public interface AeonCallable {

  int arity();

  Object call(Interpreter interpreter, List<Object> arguments);

}
