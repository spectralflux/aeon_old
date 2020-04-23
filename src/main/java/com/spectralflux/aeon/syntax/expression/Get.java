package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;

public class Get extends Expr {

    private final Expr object;
    private final Token name;

    public Get(Expr object, Token name) {
        this.object = object;
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }

    public Expr getObject() {
        return object;
    }

    public Token getName() {
        return name;
    }
}