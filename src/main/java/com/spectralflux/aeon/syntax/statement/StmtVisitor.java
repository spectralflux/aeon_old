package com.spectralflux.aeon.syntax.statement;

public interface StmtVisitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitLetStmt(Let stmt);
}
