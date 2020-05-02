package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.syntax.expression.Assign;
import com.spectralflux.aeon.syntax.expression.Binary;
import com.spectralflux.aeon.syntax.expression.Call;
import com.spectralflux.aeon.syntax.expression.Expr;
import com.spectralflux.aeon.syntax.expression.ExprVisitor;
import com.spectralflux.aeon.syntax.expression.Get;
import com.spectralflux.aeon.syntax.expression.Grouping;
import com.spectralflux.aeon.syntax.expression.Literal;
import com.spectralflux.aeon.syntax.expression.Logical;
import com.spectralflux.aeon.syntax.expression.Set;
import com.spectralflux.aeon.syntax.expression.Unary;
import com.spectralflux.aeon.syntax.expression.Variable;
import com.spectralflux.aeon.syntax.statement.Block;
import com.spectralflux.aeon.syntax.statement.Expression;
import com.spectralflux.aeon.syntax.statement.Function;
import com.spectralflux.aeon.syntax.statement.Let;
import com.spectralflux.aeon.syntax.statement.Stmt;

import com.spectralflux.aeon.syntax.statement.StmtVisitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Variable resolution step.
 */
public class Resolver implements ExprVisitor<Object>, StmtVisitor<Void>  {

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,

        // TODO only useful when classes are added
        METHOD
    }

    private final Interpreter interpreter;
    private final ErrorHandler errorHandler;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    public Resolver(ErrorHandler errorHandler, Interpreter interpreter) {
        this.errorHandler = errorHandler;
        this.interpreter = interpreter;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = scopes.peek();

        if (scope.containsKey(name.getLexeme())) {
            errorHandler.error(name,
                "Variable with this name already declared in this scope.");
        }

        scope.put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = scopes.peek();

        if (scope.containsKey(name.getLexeme())) {
            errorHandler.error(name,
                "Variable cannot be redefined with \"let\".");
        }

        scopes.peek().put(name.getLexeme(), true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }

        // Not found. Assume it is global.
    }

    @Override
    public Void visitLetStmt(Let stmt) {
        declare(stmt.getName());
        if (stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }
        define(stmt.getName());
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() &&
            scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
            errorHandler.error(expr.getName(),
                "Cannot read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitAssignExpr(Assign expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        declare(stmt.getName());
        define(stmt.getName());

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(
        Function function, FunctionType type) {

        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();

        for (Token param : function.getParams()) {
            declare(param);
            define(param);
        }
        resolve(function.getBody());
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        resolve(expr.getCallee());

        for (Expr argument : expr.getArguments()) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGetExpr(Get expr) {
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSetExpr(Set expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        resolve(expr.getRight());
        return null;
    }

}
