package com.spectralflux.aeon.syntax.statement;

import com.spectralflux.aeon.interpreter.Token;

import java.util.List;

public class Function extends Stmt {

    final Token name;
    final List<Token> params;
    final List<Stmt> body;

    public Function(Token name, List<Token> params, List<Stmt> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitFunctionStmt(this);
    }
}
