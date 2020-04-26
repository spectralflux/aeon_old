package com.spectralflux.aeon.callable;

import com.spectralflux.aeon.interpreter.Environment;
import com.spectralflux.aeon.interpreter.Interpreter;
import com.spectralflux.aeon.syntax.statement.Function;
import java.util.List;

public class AeonFunction implements AeonCallable {

  private final Environment closure;
  private final Function declaration;

  public AeonFunction(Function declaration, Environment closure) {
    this.declaration = declaration;
    this.closure = closure;
  }

  @Override
  public int arity() {
    return declaration.getParams().size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < declaration.getParams().size(); i++) {
      environment.define(declaration.getParams().get(i).getLexeme(),
          arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.getBody(), environment);
    } catch (Return returnValue) {
      return returnValue.getValue();
    }

    // TODO if nulls are not allowed, return empty type? like () in functional languages...
    return null;
  }

  @Override
  public String toString() {
    return "<fn " + declaration.getName().getLexeme() + ">";
  }

}
