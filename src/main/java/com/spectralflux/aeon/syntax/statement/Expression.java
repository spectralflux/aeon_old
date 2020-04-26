package com.spectralflux.aeon.syntax.statement;

import com.spectralflux.aeon.syntax.expression.Expr;

public class Expression extends Stmt {

  private final Expr expression;

  public Expression(Expr expression) {
    this.expression = expression;
  }

  @Override
  public <R> R accept(StmtVisitor<R> visitor) {
    return visitor.visitExpressionStmt(this);
  }

  public Expr getExpression() {
    return expression;
  }
}
