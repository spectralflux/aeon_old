package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;

public class Unary extends Expr {

  private final Token operator;
  private final Expr right;

  public Unary(Token operator, Expr right) {
    this.operator = operator;
    this.right = right;
  }

  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitUnaryExpr(this);
  }

  public Token getOperator() {
    return operator;
  }

  public Expr getRight() {
    return right;
  }
}
