package com.spectralflux.aeon.syntax.expression;

public class Literal extends Expr {

    private final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public <R> R accept(ExprVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    public Object getValue() {
        return value;
    }
}
