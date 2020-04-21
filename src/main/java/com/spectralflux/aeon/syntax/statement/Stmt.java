package com.spectralflux.aeon.syntax.statement;

public abstract class Stmt {
    abstract <R> R accept(StmtVisitor<R> visitor);
}
