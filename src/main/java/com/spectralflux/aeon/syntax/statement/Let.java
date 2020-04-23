package com.spectralflux.aeon.syntax.statement;

import com.spectralflux.aeon.interpreter.Token;
import com.spectralflux.aeon.syntax.expression.Expr;

public class Let extends Stmt {

    private final Token name;
    private final Expr initializer;

    public Let(Token name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitLetStmt(this);
    }

    public Token getName() {
        return name;
    }

    public Expr getInitializer() {
        return initializer;
    }
}