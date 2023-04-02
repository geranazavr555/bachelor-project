package com.codeforces.iomarkup.symbols;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.pl.PlExpressionVisitor;
import com.codeforces.iomarkup.symbols.struct.IoModifier;
import com.codeforces.iomarkup.symbols.struct.Struct;
import com.codeforces.iomarkup.symbols.struct.StructItem;
import com.codeforces.iomarkup.symbols.struct.VarDeclaration;
import com.codeforces.iomarkup.types.Type;
import com.codeforces.iomarkup.types.complex.FixedLengthArrayType;
import com.codeforces.iomarkup.types.complex.FixedLengthStringType;
import com.codeforces.iomarkup.types.complex.StringType;
import com.codeforces.iomarkup.types.unknown.UnknownParametrizedType;
import com.codeforces.iomarkup.types.unknown.UnknownType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RawSymbolsVisitor extends IoMarkupParserBaseVisitor<Object> {
    private final PlExpressionVisitor plExpressionVisitor = new PlExpressionVisitor();
    private final SymbolsTable predefinedSymbols = new SymbolsTable();
    private final Map<String, IoMarkupItem> nameToItem = new HashMap<>();

    @Override
    public Map<String, IoMarkupItem> visitIoMarkup(IoMarkupParser.IoMarkupContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            var child = visit(ctx.getChild(i));
            if (child instanceof IoMarkupItem ioMarkupItem) {
                String name = ioMarkupItem.getName();
                if (nameToItem.containsKey(name))
                    throw new RuntimeException(); //todo
                nameToItem.put(name, ioMarkupItem);
            } else
                throw new RuntimeException(); // todo
        }

        return nameToItem;
    }

    @Override
    public Object visitMapper(IoMarkupParser.MapperContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public Object visitEnum(IoMarkupParser.EnumContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public Object visitEnumItem(IoMarkupParser.EnumItemContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public Struct visitStruct(IoMarkupParser.StructContext ctx) {
        String name = ctx.NAME().getText();
        List<StructItem> structItems = ctx.structItem().stream().map(this::visitStructItem).toList();
        return new Struct(name, structItems);
    }

    @Override
    public StructItem visitStructItem(IoMarkupParser.StructItemContext ctx) {
        if (ctx.varDeclaration() != null)
            return visitVarDeclaration(ctx.varDeclaration());
        if (ctx.ioModifier() != null)
            return visitIoModifier(ctx.ioModifier());

        throw new RuntimeException(); // todo
    }

    @Override
    public IoModifier visitIoModifier(IoMarkupParser.IoModifierContext ctx) {
        if (ctx.EOLN_MODIFIER() != null)
            return IoModifier.EOLN;
        throw new RuntimeException(); // todo
    }

    @Override
    public VarDeclaration visitVarDeclaration(IoMarkupParser.VarDeclarationContext ctx) {
        return new VarDeclaration(
                new Variable(ctx.NAME().getText(), visitVarType(ctx.varType())),
                plExpressionVisitor.visit(ctx.condition().plExpression())
        );
    }

    @Override
    public Object visitVarMapper(IoMarkupParser.VarMapperContext ctx) {
        throw new UnsupportedOperationException(); // todo
    }

    @Override
    public Type visitVarType(IoMarkupParser.VarTypeContext ctx) {
        String typeName = ctx.NAME().getText();
        Type type = predefinedSymbols.getType(typeName).orElse(null);
        if (ctx.typeParams() != null) {
            List<PlExpression> plExpressions = visitTypeParams(ctx.typeParams());
            if (StringType.getInstance().isAssignableFrom(type)) {
                if (plExpressions.size() != 1)
                    throw new RuntimeException(); // todo
                type = new FixedLengthStringType(plExpressions.get(0));
            } else
                type = new UnknownParametrizedType(typeName, plExpressions);
        }

        if (type == null)
            type = new UnknownType(typeName);

        if (ctx.arrayParam() != null)
            type = new FixedLengthArrayType<>(type, plExpressionVisitor.visit(ctx.arrayParam().plExpression()));

        return type;
    }

    @Override
    public List<PlExpression> visitTypeParams(IoMarkupParser.TypeParamsContext ctx) {
        return ctx.plExpression().stream().map(plExpressionVisitor::visit).collect(Collectors.toList());
    }

    @Override
    public PlExpression visitCondition(IoMarkupParser.ConditionContext ctx) {
        return plExpressionVisitor.visit(ctx.plExpression());
    }

    @Override
    public Object visitPlExpression(IoMarkupParser.PlExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplLogicalOr(IoMarkupParser.PlImplLogicalOrContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplLogicalAnd(IoMarkupParser.PlImplLogicalAndContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplBitwiseOr(IoMarkupParser.PlImplBitwiseOrContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplBitwiseXor(IoMarkupParser.PlImplBitwiseXorContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplBitwiseAnd(IoMarkupParser.PlImplBitwiseAndContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplEquality(IoMarkupParser.PlImplEqualityContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplRelational(IoMarkupParser.PlImplRelationalContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplBitwiseShift(IoMarkupParser.PlImplBitwiseShiftContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplAdditiveBinary(IoMarkupParser.PlImplAdditiveBinaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplMultiplicativeBinary(IoMarkupParser.PlImplMultiplicativeBinaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplPrefixUnary(IoMarkupParser.PlImplPrefixUnaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplHighestPriority(IoMarkupParser.PlImplHighestPriorityContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlFunctionCall(IoMarkupParser.PlFunctionCallContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplFunctionArgs(IoMarkupParser.PlImplFunctionArgsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlVarBinding(IoMarkupParser.PlVarBindingContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlSubscript(IoMarkupParser.PlSubscriptContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplEqualityOp(IoMarkupParser.PlImplEqualityOpContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplRelationalOp(IoMarkupParser.PlImplRelationalOpContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplAdditiveOp(IoMarkupParser.PlImplAdditiveOpContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplMultiplicativeBinaryOp(IoMarkupParser.PlImplMultiplicativeBinaryOpContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlImplPrefixUnaryOp(IoMarkupParser.PlImplPrefixUnaryOpContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlValue(IoMarkupParser.PlValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlBoolValue(IoMarkupParser.PlBoolValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlCharValue(IoMarkupParser.PlCharValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlStringValue(IoMarkupParser.PlStringValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlNumValue(IoMarkupParser.PlNumValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visitPlIntValue(IoMarkupParser.PlIntValueContext ctx) {
        throw new UnsupportedOperationException();
    }
}
