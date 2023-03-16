package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.symbols.Variable;
import com.codeforces.iomarkup.types.complex.StringType;
import com.codeforces.iomarkup.types.primitive.*;

import java.math.BigInteger;

public class PlExpressionVisitor extends IoMarkupParserBaseVisitor<PlExpression> {
    @Override
    public PlExpression visitPlExpression(IoMarkupParser.PlExpressionContext ctx) {
        PlExpression condition = visit(ctx.plImplLogicalOr());
        if (ctx.QUESTION() == null)
            return condition;

        return new PlIfExpression(condition, visit(ctx.plExpression(0)), visit(ctx.plExpression(1)));
    }

    @Override
    public PlExpression visitPlImplLogicalOr(IoMarkupParser.PlImplLogicalOrContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplLogicalAnd(0));
        if (ctx.LOGICAL_OR() == null)
            return leftExpr;
        return new PlBinaryOperator(PlBinaryOperator.Op.LOGICAL_OR, leftExpr, visit(ctx.plImplLogicalAnd(1)));
    }

    @Override
    public PlExpression visitPlImplLogicalAnd(IoMarkupParser.PlImplLogicalAndContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplBitwiseOr(0));
        if (ctx.LOGICAL_AND() == null)
            return leftExpr;
        return new PlBinaryOperator(PlBinaryOperator.Op.LOGICAL_AND, leftExpr, visit(ctx.plImplBitwiseOr(1)));
    }

    @Override
    public PlExpression visitPlImplBitwiseOr(IoMarkupParser.PlImplBitwiseOrContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplBitwiseXor(0));
        if (ctx.BITWISE_OR() == null)
            return leftExpr;
        return new PlBinaryOperator(PlBinaryOperator.Op.BITWISE_OR, leftExpr, visit(ctx.plImplBitwiseXor(1)));
    }

    @Override
    public PlExpression visitPlImplBitwiseXor(IoMarkupParser.PlImplBitwiseXorContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplBitwiseAnd(0));
        if (ctx.BITWISE_XOR() == null)
            return leftExpr;
        return new PlBinaryOperator(PlBinaryOperator.Op.BITWISE_XOR, leftExpr, visit(ctx.plImplBitwiseAnd(1)));
    }

    @Override
    public PlExpression visitPlImplBitwiseAnd(IoMarkupParser.PlImplBitwiseAndContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplEquality(0));
        if (ctx.BITWISE_AND() == null)
            return leftExpr;
        return new PlBinaryOperator(PlBinaryOperator.Op.BITWISE_AND, leftExpr, visit(ctx.plImplEquality(1)));
    }

    @Override
    public PlExpression visitPlImplEquality(IoMarkupParser.PlImplEqualityContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplRelational(0));
        if (ctx.plImplEqualityOp() == null)
            return leftExpr;

        PlExpression rightExpr = visit(ctx.plImplRelational(1));
        if (ctx.plImplEqualityOp().EQUALS() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.EQUAL, leftExpr, rightExpr);
        if (ctx.plImplEqualityOp().NOT_EQUALS() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.NOT_EQUAL, leftExpr, rightExpr);
        throw new RuntimeException(); // todo
    }

    @Override
    public PlExpression visitPlImplRelational(IoMarkupParser.PlImplRelationalContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplBitwiseShift(0));
        if (ctx.plImplRelationalOp() == null)
            return leftExpr;

        PlExpression rightExpr = visit(ctx.plImplBitwiseShift(1));
        if (ctx.plImplRelationalOp().GREATER() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.GREATER, leftExpr, rightExpr);
        if (ctx.plImplRelationalOp().GREATER_EQUAL() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.GREATER_OR_EQUAL, leftExpr, rightExpr);
        if (ctx.plImplRelationalOp().LESS() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.LESS, leftExpr, rightExpr);
        if (ctx.plImplRelationalOp().LESS_EQUAL() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.LESS_OR_EQUAL, leftExpr, rightExpr);
        throw new RuntimeException(); // todo
    }

    @Override
    public PlExpression visitPlImplBitwiseShift(IoMarkupParser.PlImplBitwiseShiftContext ctx) {
        return visit(ctx.plImplAdditiveBinary());
    }

    @Override
    public PlExpression visitPlImplAdditiveBinary(IoMarkupParser.PlImplAdditiveBinaryContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplMultiplicativeBinary(0));
        if (ctx.plImplAdditiveOp() == null)
            return leftExpr;

        PlExpression rightExpr = visit(ctx.plImplMultiplicativeBinary(1));
        if (ctx.plImplAdditiveOp().PLUS() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.ADDITION, leftExpr, rightExpr);
        if (ctx.plImplAdditiveOp().MINUS() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.SUBTRACTION, leftExpr, rightExpr);

        throw new RuntimeException(); // todo
    }

    @Override
    public PlExpression visitPlImplMultiplicativeBinary(IoMarkupParser.PlImplMultiplicativeBinaryContext ctx) {
        PlExpression leftExpr = visit(ctx.plImplPrefixUnary(0));
        if (ctx.plImplMultiplicativeBinaryOp() == null)
            return leftExpr;

        PlExpression rightExpr = visit(ctx.plImplPrefixUnary(1));
        if (ctx.plImplMultiplicativeBinaryOp().MULTIPLICATION() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.MULTIPLICATION, leftExpr, rightExpr);
        if (ctx.plImplMultiplicativeBinaryOp().DIVISION() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.DIVISION, leftExpr, rightExpr);
        if (ctx.plImplMultiplicativeBinaryOp().MODULO() != null)
            return new PlBinaryOperator(PlBinaryOperator.Op.MODULO, leftExpr, rightExpr);

        throw new RuntimeException(); // todo
    }

    @Override
    public PlExpression visitPlImplPrefixUnary(IoMarkupParser.PlImplPrefixUnaryContext ctx) {
        PlExpression expression = visit(ctx.plImplHighestPriority());
        if (ctx.plImplPrefixUnaryOp() == null)
            return expression;

        if (ctx.plImplPrefixUnaryOp().LOGICAL_NOT() != null)
            return new PlUnaryOperator(PlUnaryOperator.Op.LOGICAL_NOT, expression);
        if (ctx.plImplPrefixUnaryOp().BITWISE_NOT() != null)
            return new PlUnaryOperator(PlUnaryOperator.Op.BITWISE_NOT, expression);
        if (ctx.plImplPrefixUnaryOp().MINUS() != null)
            return new PlUnaryOperator(PlUnaryOperator.Op.MINUS, expression);

        throw new RuntimeException(); // todo
    }

    @Override
    public PlExpression visitPlImplHighestPriority(IoMarkupParser.PlImplHighestPriorityContext ctx) {
        PlExpression expression;

        if (ctx.plExpression() != null)
            expression = visit(ctx.plExpression());
        else if (ctx.plFunctionCall() != null)
            throw new UnsupportedOperationException("Not implemented yet"); // todo
        else if (ctx.plVarBinding() != null)
            expression = visit(ctx.plVarBinding());
        else if (ctx.plValue() != null)
            expression = visit(ctx.plValue());
        else
            throw new RuntimeException(); // Unexpected case todo

        if (ctx.plSubscript() != null)
            expression = new PlSubscript(expression, visit(ctx.plSubscript()));

        return expression;
    }

    @Override
    public PlExpression visitPlFunctionCall(IoMarkupParser.PlFunctionCallContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public PlExpression visitPlImplFunctionArgs(IoMarkupParser.PlImplFunctionArgsContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public PlExpression visitPlVarBinding(IoMarkupParser.PlVarBindingContext ctx) {
        return new PlVarBinding(new Variable(ctx.NAME().getText()));
    }

    @Override
    public PlExpression visitPlSubscript(IoMarkupParser.PlSubscriptContext ctx) {
        return visit(ctx.plExpression());
    }

    @Override
    public PlExpression visitPlBoolValue(IoMarkupParser.PlBoolValueContext ctx) {
        boolean value = ctx.FALSE() == null;
        return new PlValue<>(Bool.getInstance(), value);
    }

    @Override
    public PlExpression visitPlCharValue(IoMarkupParser.PlCharValueContext ctx) {
        String raw = ctx.CHAR().getText().replace("\\'", "'");
        if (raw.length() != 3 || raw.charAt(0) != '\'' || raw.charAt(raw.length() - 1) != '\'')
            throw new RuntimeException(); // todo

        return new PlValue<>(Char.getInstance(), raw.charAt(1));
    }

    @Override
    public PlExpression visitPlStringValue(IoMarkupParser.PlStringValueContext ctx) {
        String raw = ctx.STRING().getText().replace("\\\"", "\"");
        if (raw.length() < 2 || raw.charAt(0) != '\"' || raw.charAt(raw.length() - 1) != '\"')
            throw new RuntimeException(); // todo

        return new PlValue<>(StringType.getInstance(), raw.substring(1, raw.length() - 1));
    }

    @Override
    public PlExpression visitPlNumValue(IoMarkupParser.PlNumValueContext ctx) {
        if (ctx.DOT() == null && ctx.EXPONENT() == null)
            return visit(ctx.plIntValue(0)); // integer value case

        return new PlValue<>(Float64.getInstance(), ctx.getText()); // todo
    }

    @Override
    public PlExpression visitPlIntValue(IoMarkupParser.PlIntValueContext ctx) {
        // Raw values never has a leading minus. See grammar.
        String raw = ctx.DIGITS().getText().replace("'", "").replace("_", "");
        try {
            int value = Integer.parseInt(raw);
            return new PlValue<>(Int32.getInstance(), value);
        } catch (NumberFormatException ignored) {
            // No operations
        }

        try {
            long value = Long.parseLong(raw);
            if (value < UInt32.MIN_VALUE)
                return new PlValue<>(UInt32.getInstance(), value);
            else
                return new PlValue<>(Int64.getInstance(), value);
        } catch (NumberFormatException ignored) {
            // No operations
        }

        // Ensure that value fit in uint64.
        //noinspection ResultOfMethodCallIgnored
        Long.parseUnsignedLong(raw);
        return new PlValue<>(UInt64.getInstance(), new BigInteger(raw));
    }

    // Guard methods to except incorrect usage.

    @Override
    public PlExpression visitIoMarkup(IoMarkupParser.IoMarkupContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitMapper(IoMarkupParser.MapperContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitMapperBody(IoMarkupParser.MapperBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitTypeArgument(IoMarkupParser.TypeArgumentContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitTypeArgumentParams(IoMarkupParser.TypeArgumentParamsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitTypeArgumentArrayParam(IoMarkupParser.TypeArgumentArrayParamContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitEnum(IoMarkupParser.EnumContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitEnumItem(IoMarkupParser.EnumItemContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitStruct(IoMarkupParser.StructContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitStructItem(IoMarkupParser.StructItemContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitIoModifier(IoMarkupParser.IoModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitVarDeclaration(IoMarkupParser.VarDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitVarMapper(IoMarkupParser.VarMapperContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitVarType(IoMarkupParser.VarTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitTypeParams(IoMarkupParser.TypeParamsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitArrayParam(IoMarkupParser.ArrayParamContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlExpression visitCondition(IoMarkupParser.ConditionContext ctx) {
        throw new UnsupportedOperationException();
    }
}
