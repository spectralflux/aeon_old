package com.spectralflux.aeon.syntax.expression;

public interface ExprVisitor<R> {
    R visitLiteralExpr(Literal expr);
    R visitGetExpr(Get expr);
    R visitSetExpr(Set expr);
    R visitVariableExpr(Variable expr);
    R visitAssignExpr(Assign expr);
}
