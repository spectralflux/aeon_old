package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;
import java.util.List;

public class Call extends Expr {

  private final Expr callee;
  private final Token paren;
  private final List<Expr> arguments;

  public Call(Expr callee, Token paren, List<Expr> arguments) {
    this.callee = callee;
    this.paren = paren;
    this.arguments = arguments;
  }

  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitCallExpr(this);
  }

  public Expr getCallee() {
    return callee;
  }

  public Token getParen() {
    return paren;
  }

  public List<Expr> getArguments() {
    return arguments;
  }
}
