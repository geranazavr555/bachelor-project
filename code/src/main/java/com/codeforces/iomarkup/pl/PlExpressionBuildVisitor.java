package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.symbol.Function;
import com.codeforces.iomarkup.symbol.resolve.Variable;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.PrimitiveType;
import com.codeforces.iomarkup.type.StringType;
import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@AllArgsConstructor
public class PlExpressionBuildVisitor extends IoMarkupParserBaseVisitor<PlExpression> {
    private final Scope scope;

    @Override
    public PlExpression visitPlExpression(IoMarkupParser.PlExpressionContext ctx) {
        PlExpression expression = visitPlImplLogicalOr(ctx.plImplLogicalOr(0));
        if (ctx.QUESTION() == null)
            return expression;

        return new PlIfOperator(
                expression,
                visitPlImplLogicalOr(ctx.plImplLogicalOr(1)),
                visitPlImplLogicalOr(ctx.plImplLogicalOr(2))
        );
    }

    @Override
    public PlExpression visitPlImplLogicalOr(IoMarkupParser.PlImplLogicalOrContext ctx) {
        if (isEmpty(ctx.LOGICAL_OR()))
            return visitPlImplLogicalAnd(ctx.plImplLogicalAnd(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.LOGICAL_OR,
                ctx.plImplLogicalAnd().stream().map(this::visitPlImplLogicalAnd).toList()
        );
    }

    @Override
    public PlExpression visitPlImplLogicalAnd(IoMarkupParser.PlImplLogicalAndContext ctx) {
        if (isEmpty(ctx.LOGICAL_AND()))
            return visitPlImplBitwiseOr(ctx.plImplBitwiseOr(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.LOGICAL_AND,
                ctx.plImplBitwiseOr().stream().map(this::visitPlImplBitwiseOr).toList()
        );
    }

    @Override
    public PlExpression visitPlImplBitwiseOr(IoMarkupParser.PlImplBitwiseOrContext ctx) {
        if (isEmpty(ctx.PIPE()))
            return visitPlImplBitwiseXor(ctx.plImplBitwiseXor(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.BITWISE_OR,
                ctx.plImplBitwiseXor().stream().map(this::visitPlImplBitwiseXor).toList()
        );
    }

    @Override
    public PlExpression visitPlImplBitwiseXor(IoMarkupParser.PlImplBitwiseXorContext ctx) {
        if (isEmpty(ctx.BITWISE_XOR()))
            return visitPlImplBitwiseAnd(ctx.plImplBitwiseAnd(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.BITWISE_XOR,
                ctx.plImplBitwiseAnd().stream().map(this::visitPlImplBitwiseAnd).toList()
        );
    }

    @Override
    public PlExpression visitPlImplBitwiseAnd(IoMarkupParser.PlImplBitwiseAndContext ctx) {
        if (isEmpty(ctx.BITWISE_AND()))
            return visitPlImplEquality(ctx.plImplEquality(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.BITWISE_AND,
                ctx.plImplEquality().stream().map(this::visitPlImplEquality).toList()
        );
    }

    @Override
    public PlExpression visitPlImplEquality(IoMarkupParser.PlImplEqualityContext ctx) {
        assert PlBinaryOperator.Op.EQUAL.getAssociativity() == PlBinaryOperator.Associativity.LEFT;
        assert PlBinaryOperator.Op.NOT_EQUAL.getAssociativity() == PlBinaryOperator.Associativity.LEFT;

        PlExpression expression = visitPlImplRelational(ctx.plImplRelational(0));
        for (int i = 0; i < ctx.plImplEqualityOp().size(); i++) {
            PlBinaryOperator.Op op;
            if (ctx.plImplEqualityOp().get(i).EQUALS() != null)
                op = PlBinaryOperator.Op.EQUAL;
            else
                op = PlBinaryOperator.Op.NOT_EQUAL;

            expression = new PlBinaryOperator(op, expression, visitPlImplRelational(ctx.plImplRelational(i + 1)));
        }
        return expression;
    }

    @Override
    public PlExpression visitPlImplRelational(IoMarkupParser.PlImplRelationalContext ctx) {
        PlExpression expression = visitPlImplBitwiseShift(ctx.plImplBitwiseShift(0));
        if (ctx.plImplRelationalOp() != null) {
            PlBinaryOperator.Op op;
            if (ctx.plImplRelationalOp().LESS() != null)
                op = PlBinaryOperator.Op.LESS;
            else if (ctx.plImplRelationalOp().LESS_EQUAL() != null)
                op = PlBinaryOperator.Op.LESS_OR_EQUAL;
            else if (ctx.plImplRelationalOp().GREATER() != null)
                op = PlBinaryOperator.Op.GREATER;
            else
                op = PlBinaryOperator.Op.GREATER_OR_EQUAL;

            expression = new PlBinaryOperator(op, expression, visitPlImplBitwiseShift(ctx.plImplBitwiseShift(1)));
        }
        return expression;
    }

    @Override
    public PlExpression visitPlImplBitwiseShift(IoMarkupParser.PlImplBitwiseShiftContext ctx) {
        assert PlBinaryOperator.Op.BITWISE_SHIFT_LEFT.getAssociativity() == PlBinaryOperator.Associativity.LEFT;
        assert PlBinaryOperator.Op.BITWISE_SHIFT_RIGHT.getAssociativity() == PlBinaryOperator.Associativity.LEFT;

        PlExpression expression = visitPlImplAdditiveBinary(ctx.plImplAdditiveBinary(0));
        if (ctx.plImplBitwiseShiftOp() != null) {
            PlBinaryOperator.Op op;
            if (isEmpty(ctx.plImplBitwiseShiftOp().LESS()))
                op = PlBinaryOperator.Op.BITWISE_SHIFT_RIGHT;
            else
                op = PlBinaryOperator.Op.BITWISE_SHIFT_LEFT;

            expression = new PlBinaryOperator(op, expression, visitPlImplAdditiveBinary(ctx.plImplAdditiveBinary(1)));
        }

        return expression;
    }

    @Override
    public PlExpression visitPlImplAdditiveBinary(IoMarkupParser.PlImplAdditiveBinaryContext ctx) {
        assert PlBinaryOperator.Op.ADDITION.getAssociativity() == PlBinaryOperator.Associativity.LEFT;
        assert PlBinaryOperator.Op.SUBTRACTION.getAssociativity() == PlBinaryOperator.Associativity.LEFT;

        PlExpression expression = visitPlImplMultiplicativeBinary(ctx.plImplMultiplicativeBinary(0));
        for (int i = 0; i < ctx.plImplAdditiveOp().size(); i++) {
            PlBinaryOperator.Op op;
            if (ctx.plImplAdditiveOp(i).PLUS() != null)
                op = PlBinaryOperator.Op.ADDITION;
            else
                op = PlBinaryOperator.Op.SUBTRACTION;

            expression = new PlBinaryOperator(op,
                    expression, visitPlImplMultiplicativeBinary(ctx.plImplMultiplicativeBinary(i + 1)));
        }

        return expression;
    }

    @Override
    public PlExpression visitPlImplMultiplicativeBinary(IoMarkupParser.PlImplMultiplicativeBinaryContext ctx) {
        assert PlBinaryOperator.Op.MULTIPLICATION.getAssociativity() == PlBinaryOperator.Associativity.LEFT;
        assert PlBinaryOperator.Op.MODULO.getAssociativity() == PlBinaryOperator.Associativity.LEFT;
        assert PlBinaryOperator.Op.DIVISION.getAssociativity() == PlBinaryOperator.Associativity.LEFT;

        PlExpression expression = visitPlImplPowBinary(ctx.plImplPowBinary(0));
        for (int i = 0; i < ctx.plImplPowBinary().size() - 1; i++) {
            PlBinaryOperator.Op op;
            if (ctx.plImplMultiplicativeBinaryOp(i).MULTIPLICATION() != null)
                op = PlBinaryOperator.Op.MULTIPLICATION;
            else if (ctx.plImplMultiplicativeBinaryOp(i).MODULO() != null)
                op = PlBinaryOperator.Op.MODULO;
            else
                op = PlBinaryOperator.Op.DIVISION;

            expression = new PlBinaryOperator(op, expression, visitPlImplPowBinary(ctx.plImplPowBinary(i + 1)));
        }

        return expression;
    }

    @Override
    public PlExpression visitPlImplPowBinary(IoMarkupParser.PlImplPowBinaryContext ctx) {
        if (isEmpty(ctx.POW()))
            return visitPlImplPrefixUnary(ctx.plImplPrefixUnary(0));

        return new PlBinaryOperator(
                PlBinaryOperator.Op.POW,
                ctx.plImplPrefixUnary().stream().map(this::visitPlImplPrefixUnary).toList()
        );
    }

    @Override
    public PlExpression visitPlImplPrefixUnary(IoMarkupParser.PlImplPrefixUnaryContext ctx) {
        PlExpression expression = visitPlImplHighestPriority(ctx.plImplHighestPriority());
        for (int i = ctx.plImplPrefixUnaryOp().size() - 1; i >= 0; i--) {
            var opCtx = ctx.plImplPrefixUnaryOp().get(i);
            PlUnaryOperator.Op op;
            if (opCtx.MINUS() != null)
                op = PlUnaryOperator.Op.MINUS;
            else if (opCtx.LOGICAL_NOT() != null)
                op = PlUnaryOperator.Op.LOGICAL_NOT;
            else
                op = PlUnaryOperator.Op.BITWISE_NOT;
            expression = new PlUnaryOperator(op, expression);
        }
        return expression;
    }

    @Override
    public PlExpression visitPlImplHighestPriority(IoMarkupParser.PlImplHighestPriorityContext ctx) {
        PlExpression expression = Stream.of(ctx.plExpression(), ctx.plFunctionCall(), ctx.plValue(), ctx.plVarBinding())
                .filter(Objects::nonNull).map(this::visit).findAny().orElseThrow(); // todo
        for (var plSubscriptContext : ctx.plSubscript()) {
            expression = new PlSubscript(expression, visitPlSubscript(plSubscriptContext));
        }

        if (expression instanceof PlVarBinding plVarBinding && !isEmpty(ctx.plVarLocate())) {
            for (var plVarLocateContext : ctx.plVarLocate()) {
                plVarBinding.getFieldLocate().add(new PlFieldLocate(plVarLocateContext.NAME().getText(), null));
                if (!isEmpty(plVarLocateContext.plSubscript()))
                    plVarLocateContext.plSubscript().stream()
                            .map(x -> new PlFieldLocate(null, visitPlSubscript(x)))
                            .forEach(plVarBinding.getFieldLocate()::add);
            }
            return plVarBinding;
        } else
            return expression;
    }

    @Override
    public PlExpression visitPlFunctionCall(IoMarkupParser.PlFunctionCallContext ctx) {
        Function function = scope.getFunction(ctx.NAME().getText());
        if (function == null)
            throw new RuntimeException(); // todo

        return new PlFunctionCall(
                function,
                Optional.ofNullable(ctx.plImplFunctionArgs())
                        .map(
                                argsCtx -> argsCtx.plExpression()
                                        .stream()
                                        .map(this::visitPlExpression)
                                        .toList()
                        ).orElseGet(List::of)
        );
    }

    @Override
    public PlExpression visitPlVarBinding(IoMarkupParser.PlVarBindingContext ctx) {
        Variable variable = scope.getVariable(ctx.NAME().getText());
        if (variable == null)
            throw new RuntimeException(); // todo
        return new PlVarBinding(variable, new ArrayList<>());
    }

    @Override
    public PlExpression visitPlSubscript(IoMarkupParser.PlSubscriptContext ctx) {
        return visitPlExpression(ctx.plExpression());
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
        String rawText = ctx.CHAR().getText();
        if (rawText.length() < 2 || rawText.charAt(0) != '"' || rawText.charAt(rawText.length() - 1) != '"')
            throw new RuntimeException(); // todo

        String value = rawText.substring(1, rawText.length() - 1).replace("\\n", "\n");
        if (value.length() != 1)
            throw new RuntimeException(); // todo

        return new PlValue<>(PrimitiveType.CHAR, value.charAt(0));
    }

    @Override
    public PlExpression visitPlStringValue(IoMarkupParser.PlStringValueContext ctx) {
        String rawText = ctx.STRING().getText();
        if (rawText.length() < 2 || rawText.charAt(0) != '"' || rawText.charAt(rawText.length() - 1) != '"')
            throw new RuntimeException(); // todo

        String value = rawText.substring(1, rawText.length() - 1).replace("\\n", "\n");
        return new PlValue<>(StringType.getInstance(), value);
    }

    private static final Pattern numPattern =
            Pattern.compile("^(\\d+)(u)?(l)?(\\.\\d+)?(e\\d+)?(f)?$", Pattern.CASE_INSENSITIVE);

    @Override
    public PlExpression visitPlNumValue(IoMarkupParser.PlNumValueContext ctx) {
        String text = ctx.NUM_VALUE().getText().replace("_", "").replace("'", "");
        Matcher matcher = numPattern.matcher(text);
        if (!matcher.matches())
            throw new RuntimeException(); // todo

        String intPart = matcher.group(1);
        String uModifier = matcher.group(2);
        String lModifier = matcher.group(3);
        String floatPart = matcher.group(4);
        String expPart = matcher.group(5);
        String fModifier = matcher.group(6);

        boolean isInteger = !isEmpty(uModifier) || !isEmpty(lModifier);
        boolean isFloat = !isEmpty(floatPart) || !isEmpty(expPart) || !isEmpty(fModifier);
        if (isInteger && isFloat)
            throw new RuntimeException();

        isInteger = !isFloat;

        if (isInteger) {
            if (isEmpty(uModifier) && isEmpty(lModifier)) {
                int value = Integer.parseInt(intPart); // TODO: 23.03.2023
                return new PlValue<>(PrimitiveType.INT32, value);
            } else if (!isEmpty(uModifier) && isEmpty(lModifier)) {
                long value = Long.parseLong(intPart); // TODO: 23.03.2023
                return new PlValue<>(PrimitiveType.UINT32, value);
            } else if (isEmpty(uModifier) && !isEmpty(lModifier)) {
                long value = Long.parseLong(intPart); // TODO: 23.03.2023
                return new PlValue<>(PrimitiveType.INT64, value);
            } else {
                //noinspection ResultOfMethodCallIgnored
                Long.parseUnsignedLong(intPart); // TODO: 23.03.2023
                return new PlValue<>(PrimitiveType.UINT64, new BigInteger(intPart));
            }
        } else {
            if (isEmpty(fModifier))
                return new PlValue<>(PrimitiveType.FLOAT64, intPart);
            else
                return new PlValue<>(PrimitiveType.FLOAT32, intPart);
        }
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private static boolean isEmpty(Collection<?> s) {
        return s == null || s.isEmpty();
    }
}
