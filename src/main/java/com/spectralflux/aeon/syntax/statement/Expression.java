package com.spectralflux.aeon.syntax.statement;

import com.spectralflux.aeon.syntax.expression.Expr;

public class Expression extends Stmt {

    final Expr expression;

    public Expression(Expr expression) {
        this.expression = expression;
    }

    @Override
    <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitExpressionStmt(this);
    }

}
