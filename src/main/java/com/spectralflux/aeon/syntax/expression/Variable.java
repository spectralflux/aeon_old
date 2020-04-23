package com.spectralflux.aeon.syntax.expression;

import com.spectralflux.aeon.interpreter.Token;

public class Variable extends Expr {

    private final Token name;

    public Variable(Token name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitVariableExpr(this);
    }

    public Token getName() {
        return name;
    }
}
