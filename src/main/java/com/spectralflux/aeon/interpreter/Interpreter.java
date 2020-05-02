package com.spectralflux.aeon.interpreter;

import com.spectralflux.aeon.callable.AeonCallable;
import com.spectralflux.aeon.callable.AeonFunction;
import com.spectralflux.aeon.error.ErrorHandler;
import com.spectralflux.aeon.error.RuntimeError;
import com.spectralflux.aeon.lib.Print;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

  private final ErrorHandler errorHandler;

  private final Environment globals = new Environment();
  private Environment environment = globals;
  private final Map<Expr, Integer> locals = new HashMap<>();

  public Interpreter(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
    defineNativeFunctions();
  }

  void resolve(Expr expr, int depth) {
    locals.put(expr, depth);
  }

  private void defineNativeFunctions() {
    globals.define("print", new Print());
  }

  public void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      errorHandler.runtimeError(error);
    }
  }

  public void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;

      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  public Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    return expr.getValue();
  }

  @Override
  public Object visitGroupingExpr(Grouping expr) {
    return evaluate(expr.getExpression());
  }

  @Override
  public Object visitUnaryExpr(Unary expr) {
    Object right = evaluate(expr.getRight());

    switch (expr.getOperator().getType()) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        if (right instanceof Integer) {
          return -(int) right;
        } else {
          return -(double) right;
        }
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitBinaryExpr(Binary expr) {
    Object left = evaluate(expr.getLeft());
    Object right = evaluate(expr.getRight());

    switch (expr.getOperator().getType()) {
      case GREATER:
        checkNumberOperands(expr.getOperator(), left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.getOperator(), left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperands(expr.getOperator(), left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperands(expr.getOperator(), left, right);
        return (double) left <= (double) right;
      case MINUS:
        checkNumberOperands(expr.getOperator(), left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }

        if (left instanceof Integer && right instanceof Integer) {
          return (int) left + (int) right;
        }

        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }

        throw new RuntimeError(expr.getOperator(),
            "Operands must be two integers, two floats, or two strings.");
      case SLASH:
        checkNumberOperands(expr.getOperator(), left, right);
        if (right.equals(0.0) || right.equals(0)) {
          throw new RuntimeError(expr.getOperator(), "Cannot divide by zero.");
        }
        if (left instanceof Integer && right instanceof Integer) {
          return (int) left / (int) right;
        } else if (left instanceof Double && right instanceof Double) {
          return (double) left / (double) right;
        }
      case STAR:
        checkNumberOperands(expr.getOperator(), left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int) left * (int) right;
        } else if (left instanceof Double && right instanceof Double) {
          return (double) left * (double) right;
        }
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
    }

    // Unreachable.
    return null;
  }

  @Override
  public Object visitCallExpr(Call expr) {
    Object callee = evaluate(expr.getCallee());

    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.getArguments()) {
      arguments.add(evaluate(argument));
    }

    if (!(callee instanceof AeonCallable)) {
      throw new RuntimeError(expr.getParen(),
          "Can only call functions and classes.");
    }

    AeonCallable function = (AeonCallable) callee;
    if (arguments.size() != function.arity()) {
      throw new RuntimeError(expr.getParen(), "Expected " +
          function.arity() + " arguments but got " +
          arguments.size() + ".");
    }

    return function.call(this, arguments);
  }

  @Override
  public Object visitVariableExpr(Variable expr) {
    return lookUpVariable(expr.getName(), expr);
  }

  private Object lookUpVariable(Token name, Expr expr) {
    Integer distance = locals.get(expr);
    if (distance != null) {
      return environment.getAt(distance, name.getLexeme());
    } else {
      return globals.get(name);
    }
  }

  private boolean isTruthy(Object object) {
    if (object == null) {
      return false;
    }
    if (object instanceof Boolean) {
      return (boolean) object;
    }
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    // TODO this might not work?
    return a.equals(b);
  }

  private void checkNumberOperands(Token operator,
      Object left, Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }

    if (left instanceof Integer && right instanceof Integer) {
      return;
    }

    throw new RuntimeError(operator, "Operands must both be integers or floats.");
  }

  @Override
  public Void visitBlockStmt(Block stmt) {
    executeBlock(stmt.getStatements(), new Environment(environment));
    return null;
  }

  @Override
  public Object visitAssignExpr(Assign expr) {
    Object value = evaluate(expr.getValue());
    Integer distance = locals.get(expr);
    if (distance != null) {
      environment.assignAt(distance, expr.getName(), value);
    } else {
      globals.assign(expr.getName(), value);
    }
    return value;
  }

  @Override
  public Void visitFunctionStmt(Function stmt) {
    AeonFunction function = new AeonFunction(stmt, environment);
    environment.define(stmt.getName().getLexeme(), function);
    return null;
  }

  // TODO implement visitor methods

  @Override
  public Object visitGetExpr(Get expr) {
    return null;
  }

  @Override
  public Object visitSetExpr(Set expr) {
    return null;
  }

  @Override
  public Void visitLetStmt(Let stmt) {
    return null;
  }

  @Override
  public Object visitLogicalExpr(Logical expr) {
    return null;
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    evaluate(stmt.getExpression());
    return null;
  }
}
