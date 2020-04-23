package com.spectralflux.aeon.syntax.expression;

public abstract class Expr {
    public abstract <R> R accept(ExprVisitor<R> visitor);
}
