package com.codeforces.iomarkup.generation.java;

import com.codeforces.iomarkup.pl.*;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;
import com.codeforces.iomarkup.type.Type;

import java.util.stream.Collectors;

class JavaPlExpressionTranslator extends JavaTargetTranslator {
    private final String overridePath;
    private final PlExpression expression;

    public JavaPlExpressionTranslator(PlExpression expression) {
        this(expression, null);
    }

    public JavaPlExpressionTranslator(PlExpression expression, String overridePath) {
        this.expression = expression;
        this.overridePath = overridePath;
    }

    public String translate() {
        return translate(expression);
    }

    private String translate(PlExpression expression) {
        if (expression instanceof PlBinaryOperator plBinaryOperator)
            return visitBinaryOperator(plBinaryOperator);
        else if (expression instanceof PlFunctionCall plFunctionCall)
            return visitFunctionCall(plFunctionCall);
        else if (expression instanceof PlIfOperator plIfOperator)
            return visitIfOperator(plIfOperator);
        else if (expression instanceof PlSubscript plSubscript)
            return visitSubscript(plSubscript);
        else if (expression instanceof PlUnaryOperator plUnaryOperator)
            return visitUnaryOperator(plUnaryOperator);
        else if (expression instanceof PlValue<?> plValue)
            return visitValue(plValue);
        else if (expression instanceof PlVarBinding plVarBinding)
            return visitVarBinding(plVarBinding);
        else throw new AssertionError();
    }

    private String visitBinaryOperator(PlBinaryOperator plBinaryOperator) {
        if (plBinaryOperator.getOp() == PlBinaryOperator.Op.POW) {
          throw new RuntimeException(); // todo
        }

        return plBinaryOperator.getExpressions().stream().map(this::translate).collect(Collectors.joining(
                " " + switch (plBinaryOperator.getOp()) {
                    case MULTIPLICATION -> "*";
                    case DIVISION -> "/";
                    case MODULO -> "%";
                    case ADDITION -> "+";
                    case SUBTRACTION -> "-";
                    case GREATER -> ">";
                    case GREATER_OR_EQUAL -> ">=";
                    case LESS -> "<";
                    case LESS_OR_EQUAL -> "<=";
                    case EQUAL -> "==";
                    case NOT_EQUAL -> "!=";
                    case BITWISE_AND -> "&";
                    case BITWISE_XOR -> "^";
                    case BITWISE_OR -> "|";
                    case LOGICAL_AND -> "&&";
                    case LOGICAL_OR -> "||";
                    case BITWISE_SHIFT_LEFT -> "<<";
                    case BITWISE_SHIFT_RIGHT -> ">>";
                    default -> throw new IllegalStateException("Unexpected value: " + plBinaryOperator.getOp());
                } + " "
        ));
    }

    private String visitFunctionCall(PlFunctionCall plFunctionCall) {
        if (!"len".equals(plFunctionCall.getFunction().getName()))
            throw new AssertionError();

        return translate(plFunctionCall.getArgExpressions().get(0)) + ".size()"; // todo .length()
    }

    private String visitIfOperator(PlIfOperator plIfOperator) {
        return "((%s) ? (%s) : (%s))".formatted(
                translate(plIfOperator.getCondition()),
                translate(plIfOperator.getTrueExpression()),
                translate(plIfOperator.getFalseExpression())
        );
    }

    private String visitSubscript(PlSubscript plSubscript) { // todo .charAt()
        return translate(plSubscript.getArrayExpression()) + ".get(" + translate(plSubscript.getIndexExpression()) + ")";
    }

    private String visitUnaryOperator(PlUnaryOperator plUnaryOperator) {
        return switch (plUnaryOperator.getOp()) {
            case LOGICAL_NOT -> "!";
            case BITWISE_NOT -> "~";
            case MINUS -> "-";
        } + translate(plUnaryOperator.getExpression());
    }

    private <T> String visitValue(PlValue<T> value) {
        Type type = value.getType();
        T val = value.getValue();
        if (type instanceof PrimitiveType primitiveType) {
            return switch (primitiveType) {
                case BOOL, FLOAT32, INT32 -> val.toString();
                case CHAR -> "'" + val + "'";
                case UINT32, INT64 -> val + "L";
                case UINT64 -> "new java.math.BigInteger(\"%s\")".formatted(val);
                case FLOAT64 -> val.toString() + "F";
            };
        } else if (type instanceof StringType) {
            return "\"" + val + "\"";
        } else throw new AssertionError();
    }

    private String visitVarBinding(PlVarBinding plVarBinding) {
        var var = plVarBinding.getVariable();
        var sb = new StringBuilder();

        if (plVarBinding.getFieldLocate() == null || plVarBinding.getFieldLocate().isEmpty()) {
            if (overridePath == null) {
                var path = var.getPath();
                for (int i = 0; i < path.size(); i++) {
                    sb.append(path.get(i).iteration() ? ".get(" : "")
                            .append(path.get(i).name())
                            .append(path.get(i).iteration() ? ")" : "");
                    if (i == path.size() - 1 || i < path.size() - 1 && !path.get(i + 1).iteration())
                        sb.append(".");
                }
            } else if (!overridePath.isEmpty()) {
                sb.append(overridePath).append('.');
            }
            sb.append(var.getName());
        } else {
            sb.append(var.getName());
            for (int i = 0; i < plVarBinding.getFieldLocate().size(); ++i) {
                var cur = plVarBinding.getFieldLocate().get(i);
                if (cur.iterExpr() != null) {
                    sb.append(".get(").append(translate(cur.iterExpr())).append(")");
                } else {
                    sb.append(".").append(cur.name());
                }
            }
        }
        return sb.toString();
    }
}
