package com.spectralflux.aeon.syntax.statement;

import com.spectralflux.aeon.interpreter.Token;

import java.util.List;

public class Function extends Stmt {

    private final Token name;
    private final List<Token> params;
    private final List<Stmt> body;

    public Function(Token name, List<Token> params, List<Stmt> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public <R> R accept(StmtVisitor<R> visitor) {
        return visitor.visitFunctionStmt(this);
    }

    public Token getName() {
        return name;
    }

    public List<Token> getParams() {
        return params;
    }

    public List<Stmt> getBody() {
        return body;
    }
}
