package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.types.PrimitiveType;
import com.codeforces.iomarkup.types.StringType;

import java.math.BigInteger;

public class PlExpressionVisitor extends IoMarkupParserBaseVisitor<PlExpression> {
    @Override
    public PlExpression visitPlExpression(IoMarkupParser.PlExpressionContext ctx) {
        return super.visitPlExpression(ctx);
    }

    @Override
    public PlExpression visitPlImplLogicalOr(IoMarkupParser.PlImplLogicalOrContext ctx) {
        return super.visitPlImplLogicalOr(ctx);
    }

    @Override
    public PlExpression visitPlImplLogicalAnd(IoMarkupParser.PlImplLogicalAndContext ctx) {
        return super.visitPlImplLogicalAnd(ctx);
    }

    @Override
    public PlExpression visitPlImplBitwiseOr(IoMarkupParser.PlImplBitwiseOrContext ctx) {
        return super.visitPlImplBitwiseOr(ctx);
    }

    @Override
    public PlExpression visitPlImplBitwiseXor(IoMarkupParser.PlImplBitwiseXorContext ctx) {
        return super.visitPlImplBitwiseXor(ctx);
    }

    @Override
    public PlExpression visitPlImplBitwiseAnd(IoMarkupParser.PlImplBitwiseAndContext ctx) {
        return super.visitPlImplBitwiseAnd(ctx);
    }

    @Override
    public PlExpression visitPlImplEquality(IoMarkupParser.PlImplEqualityContext ctx) {
        return super.visitPlImplEquality(ctx);
    }

    @Override
    public PlExpression visitPlImplRelational(IoMarkupParser.PlImplRelationalContext ctx) {
        return super.visitPlImplRelational(ctx);
    }

    @Override
    public PlExpression visitPlImplBitwiseShift(IoMarkupParser.PlImplBitwiseShiftContext ctx) {
        return super.visitPlImplBitwiseShift(ctx);
    }

    @Override
    public PlExpression visitPlImplAdditiveBinary(IoMarkupParser.PlImplAdditiveBinaryContext ctx) {
        return super.visitPlImplAdditiveBinary(ctx);
    }

    @Override
    public PlExpression visitPlImplMultiplicativeBinary(IoMarkupParser.PlImplMultiplicativeBinaryContext ctx) {
        return super.visitPlImplMultiplicativeBinary(ctx);
    }

    @Override
    public PlExpression visitPlImplPrefixUnary(IoMarkupParser.PlImplPrefixUnaryContext ctx) {
        return super.visitPlImplPrefixUnary(ctx);
    }

    @Override
    public PlExpression visitPlImplHighestPriority(IoMarkupParser.PlImplHighestPriorityContext ctx) {
        return super.visitPlImplHighestPriority(ctx);
    }

    @Override
    public PlExpression visitPlFunctionCall(IoMarkupParser.PlFunctionCallContext ctx) {
        return super.visitPlFunctionCall(ctx);
    }

    @Override
    public PlExpression visitPlImplFunctionArgs(IoMarkupParser.PlImplFunctionArgsContext ctx) {
        return super.visitPlImplFunctionArgs(ctx);
    }

    @Override
    public PlExpression visitPlVarLocate(IoMarkupParser.PlVarLocateContext ctx) {
        return super.visitPlVarLocate(ctx);
    }

    @Override
    public PlExpression visitPlVarBinding(IoMarkupParser.PlVarBindingContext ctx) {
        return super.visitPlVarBinding(ctx);
    }

    @Override
    public PlExpression visitPlSubscript(IoMarkupParser.PlSubscriptContext ctx) {
        return super.visitPlSubscript(ctx);
    }

    @Override
    public PlExpression visitPlImplEqualityOp(IoMarkupParser.PlImplEqualityOpContext ctx) {
        return super.visitPlImplEqualityOp(ctx);
    }

    @Override
    public PlExpression visitPlImplRelationalOp(IoMarkupParser.PlImplRelationalOpContext ctx) {
        return super.visitPlImplRelationalOp(ctx);
    }

    @Override
    public PlExpression visitPlImplAdditiveOp(IoMarkupParser.PlImplAdditiveOpContext ctx) {
        return super.visitPlImplAdditiveOp(ctx);
    }

    @Override
    public PlExpression visitPlImplMultiplicativeBinaryOp(IoMarkupParser.PlImplMultiplicativeBinaryOpContext ctx) {
        return super.visitPlImplMultiplicativeBinaryOp(ctx);
    }

    @Override
    public PlExpression visitPlImplPrefixUnaryOp(IoMarkupParser.PlImplPrefixUnaryOpContext ctx) {
        return super.visitPlImplPrefixUnaryOp(ctx);
    }

    @Override
    public PlExpression visitPlValue(IoMarkupParser.PlValueContext ctx) {
        return visit(ctx.getChild(0));
    }

    @Override
    public PlExpression visitPlBoolValue(IoMarkupParser.PlBoolValueContext ctx) {
        boolean value = ctx.FALSE() == null;
        return new PlValue<>(PrimitiveType.BOOL, value);
    }

    @Override
    public PlExpression visitPlCharValue(IoMarkupParser.PlCharValueContext ctx) {
        String raw = ctx.CHAR().getText().replace("\\'", "'");
        if (raw.length() != 3 || raw.charAt(0) != '\'' || raw.charAt(raw.length() - 1) != '\'')
            throw new RuntimeException(); // todo

        return new PlValue<>(PrimitiveType.CHAR, raw.charAt(1));
    }

    @Override
    public PlExpression visitPlStringValue(IoMarkupParser.PlStringValueContext ctx) {
        String raw = ctx.STRING().getText().replace("\\\"", "\"");
        if (raw.length() < 2 || raw.charAt(0) != '\"' || raw.charAt(raw.length() - 1) != '\"')
            throw new RuntimeException(); // todo

        var value = raw.substring(1, raw.length() - 1);
        return new PlValue<>(new StringType(new PlValue<>(PrimitiveType.UINT32, value.length())), value);
    }

    @Override
    public PlExpression visitPlNumValue(IoMarkupParser.PlNumValueContext ctx) {
        if (ctx.DOT() == null && ctx.EXPONENT() == null)
            return visit(ctx.plIntValue(0));

        return new PlValue<>(PrimitiveType.FLOAT64, ctx.getText()); // todo
    }

    @Override
    public PlExpression visitPlIntValue(IoMarkupParser.PlIntValueContext ctx) {
        // Raw values never has a leading minus. See grammar.
        String raw = ctx.DIGITS().getText().replace("'", "").replace("_", "");
        try {
            int value = Integer.parseInt(raw);
            return new PlValue<>(PrimitiveType.INT32, value);
        } catch (NumberFormatException ignored) {
            // No operations
        }

        try {
            long value = Long.parseLong(raw);
            if (0 < value && value < (1L << 32) - 1)
                return new PlValue<>(PrimitiveType.UINT32, value);
            else
                return new PlValue<>(PrimitiveType.INT64, value);
        } catch (NumberFormatException ignored) {
            // No operations
        }

        // Ensure that value fit in uint64.
        //noinspection ResultOfMethodCallIgnored
        Long.parseUnsignedLong(raw);
        return new PlValue<>(PrimitiveType.UINT64, new BigInteger(raw));
    }
}
