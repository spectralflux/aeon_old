package com.spectralflux.aeon.syntax.expression;

public interface ExprVisitor<R> {
    R visitLiteralExpr(Literal expr);
    R visitGetExpr(Get expr);
    R visitSetExpr(Set expr);
    R visitVariableExpr(Variable expr);
    R visitAssignExpr(Assign expr);
    R visitCallExpr(Call expr);
    R visitLogicalExpr(Logical expr);
    R visitBinaryExpr(Binary expr);
    R visitUnaryExpr(Unary expr);
    R visitGroupingExpr(Grouping expr);
}
