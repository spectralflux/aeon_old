package com.spectralflux.aeon.syntax.statement;

import java.util.List;

public class Block extends Stmt {

  private final List<Stmt> statements;

  public Block(List<Stmt> statements) {
    this.statements = statements;
  }

  @Override
  public <R> R accept(StmtVisitor<R> visitor) {
    return visitor.visitBlockStmt(this);
  }

  public List<Stmt> getStatements() {
    return statements;
  }
}
