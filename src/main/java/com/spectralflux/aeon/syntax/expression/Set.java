package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;

public class Set extends Expr {

    private final Expr object;
    private final Token name;
    private final Expr value;

    public Set(Expr object, Token name, Expr value) {
        this.object = object;
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitSetExpr(this);
    }

    public Expr getObject() {
        return object;
    }

    public Token getName() {
        return name;
    }

    public Expr getValue() {
        return value;
    }
}
