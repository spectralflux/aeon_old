package com.spectralflux.aeon.syntax.statement;

public interface StmtVisitor<R> {
    R visitFunctionStmt(Function stmt);
}
