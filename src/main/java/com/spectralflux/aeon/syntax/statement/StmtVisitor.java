package com.spectralflux.aeon.syntax.statement;

public interface StmtVisitor<R> {
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitLetStmt(Let stmt);
}
