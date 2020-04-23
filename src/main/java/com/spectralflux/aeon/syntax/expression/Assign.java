package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;

public class Assign extends Expr {

    private final Token name;
    private final Expr value;

    public Assign(Token name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }

    public Token getName() {
        return name;
    }

    public Expr getValue() {
        return value;
    }
}
