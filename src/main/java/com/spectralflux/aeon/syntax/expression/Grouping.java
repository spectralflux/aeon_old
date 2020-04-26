package com.spectralflux.aeon.syntax.expression;

public class Grouping extends Expr {

  private final Expr expression;

  public Grouping(Expr expression) {
    this.expression = expression;
  }

  @Override
  public <R> R accept(ExprVisitor<R> visitor) {
    return visitor.visitGroupingExpr(this);
  }

  public Expr getExpression() {
    return expression;
  }
}
