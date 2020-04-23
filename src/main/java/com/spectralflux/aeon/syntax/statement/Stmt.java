package com.spectralflux.aeon.syntax.statement;

public abstract class Stmt {
    public abstract <R> R accept(StmtVisitor<R> visitor);
}
