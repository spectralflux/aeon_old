package com.spectralflux.aeon.lib;

import com.spectralflux.aeon.callable.AeonCallable;
import com.spectralflux.aeon.interpreter.Interpreter;
import com.spectralflux.aeon.syntax.expression.Literal;
import java.util.List;

public class Print implements AeonCallable, NativeFunction {

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    String printBody = "";

    if (arguments != null && !arguments.isEmpty()) {
      printBody = interpreter.evaluate(new Literal(arguments.get(0))).toString();
    }

    System.out.println(printBody);

    // TODO are nulls allowed?
    return null;
  }

}
