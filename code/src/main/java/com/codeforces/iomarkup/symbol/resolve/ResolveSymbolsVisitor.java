package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.pl.PlExpressionVisitor;
import com.codeforces.iomarkup.symbol.ConstructorArgument;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResolveSymbolsVisitor extends IoMarkupParserBaseVisitor<Object> {
    private final Scope globalScope;
    private final List<Scope> scopeStack = new ArrayList<>();
    private Symbol currentSymbol = null;

    private Scope scope() {
        return scopeStack.get(scopeStack.size() - 1);
    }

    private void pushScope() {
        scopeStack.add(scope().newChild());
    }

    private void popScope() {
        scopeStack.remove(scopeStack.size() - 1);
    }

    public ResolveSymbolsVisitor(Scope globalScope) {
        this.globalScope = globalScope;
    }

    @Override
    public Scope visitIoMarkup(IoMarkupParser.IoMarkupContext ctx) {
        scopeStack.clear();
        scopeStack.add(globalScope);
        ctx.namedStruct().forEach(this::visitNamedStruct);
        return globalScope;
    }

    @Override
    public ConstructorWithBody visitNamedStruct(IoMarkupParser.NamedStructContext ctx) {
        ConstructorWithBody constructor = scope().getConstructor(ctx.NAME().getText());
        if (constructor == null)
            throw new RuntimeException();

        currentSymbol = constructor;
        pushScope();
        for (ConstructorArgument argument : constructor.getArguments()) {
            NamedType type = scope().getNamedType(argument.getType());
            if (type == null)
                throw new RuntimeException();

            if (type.type() instanceof StructType)
                currentSymbol.addRequirement(globalScope.getConstructor(type.getName()));

            scope().pushVariable(new Variable(argument.getName(), type.type()));
        }

        visitStruct(ctx.struct()).forEach(constructor::addBodyItem);
        popScope();
        currentSymbol = null;

        return constructor;
    }

    @Override
    public List<ConstructorItem> visitStruct(IoMarkupParser.StructContext ctx) {
        List<ConstructorItem> result = new ArrayList<>();
        for (var structItemCtx : ctx.structItem()) {
            if (structItemCtx.conditionalAlternative() != null) {
                ConstructorIfAlt ifAlt = visitConditionalAlternative(structItemCtx.conditionalAlternative());
                result.add(ifAlt);

            } else if (structItemCtx.variableDeclaration() != null) {
                Variable variable = visitVariableDeclaration(structItemCtx.variableDeclaration());
                scope().pushVariable(variable);
                result.add(variable);

            } else if (structItemCtx.ioModifier() != null) {
                IoModifier ioModifier = visitIoModifier(structItemCtx.ioModifier());
                result.add(ioModifier);

            } else assert false;
        }
        return result;
    }

    @Override
    public ConstructorIfAlt visitConditionalAlternative(IoMarkupParser.ConditionalAlternativeContext ctx) {
        var plExpression = new PlExpressionVisitor(scope()).visitPlExpression(ctx.plExpression());
        pushScope();
        var trueItems = visitStruct(ctx.struct(0));
        popScope();
        pushScope();
        var falseItems = visitStruct(ctx.struct(1));
        popScope();
        return new ConstructorIfAlt(plExpression, trueItems, falseItems);
    }

    @Override
    public IoModifier visitIoModifier(IoMarkupParser.IoModifierContext ctx) {
        assert IoModifier.values().length == 1;
        assert ctx.EOLN_MODIFIER() != null;
        return IoModifier.EOLN;
    }

    @Override
    public Variable visitVariableDeclaration(IoMarkupParser.VariableDeclarationContext ctx) {
        String name = ctx.NAME().getText();
        if (scope().containsName(name))
            throw new RuntimeException();

        var variableDescription = visitVariableType(ctx.variableType());
        var variable = new Variable(name, variableDescription, null);
        if (ctx.variableConstraint() != null) {
            pushScope();
            scope().pushVariable(variable);
            variable = new Variable(
                    ctx.NAME().getText(),
                    variableDescription,
                    visitVariableConstraint(ctx.variableConstraint())
            );
            popScope();
        }

        if (variableDescription instanceof ArrayDescription<?>)
            popScope();

        return variable;
    }

    @Override
    public VariableDescription visitVariableType(IoMarkupParser.VariableTypeContext ctx) {
        if (ctx.arrayOfUnnamedStruct() != null)
            return visitArrayOfUnnamedStruct(ctx.arrayOfUnnamedStruct());

        Type type = visitNamedType(ctx.namedType());
        VariableDescription result = type;

        List<ArrayParameters> arrayParameters = null;
        if (!isEmpty(ctx.arrayParameters())) {
            pushScope();
            arrayParameters = ctx.arrayParameters().stream().map(this::visitArrayParameters).toList();
        }

        if (ctx.namedTypeParameters() != null) {
            if (!TypeCharacteristic.PARAMETRIZED.is(type))
                throw new RuntimeException(); // todo

            result = new ParametrizedDescription(type, visitNamedTypeParameters(ctx.namedTypeParameters()));
        }

        if (arrayParameters != null) {
            result = new NamedStructArrayDescription(arrayParameters, result);
        }
        return result;
    }

    @Override
    public UnnamedStructArrayDescription visitArrayOfUnnamedStruct(IoMarkupParser.ArrayOfUnnamedStructContext ctx) {
        pushScope();
        List<ArrayParameters> arrayParameters = ctx.arrayParameters().stream().map(this::visitArrayParameters).toList();
        List<ConstructorItem> innerStruct = visitStruct(ctx.struct());
        return new UnnamedStructArrayDescription(arrayParameters, innerStruct);
    }

    @Override
    public PlExpression visitVariableConstraint(IoMarkupParser.VariableConstraintContext ctx) {
        return new PlExpressionVisitor(scope()).visitPlExpression(ctx.plExpression());
    }

    @Override
    public Type visitNamedType(IoMarkupParser.NamedTypeContext ctx) {
        String name = ctx.NAME().getText();
        var type = scope().getNamedType(name);
        if (type == null)
            throw new RuntimeException(); // todo
        if (type.type() instanceof StructType)
            currentSymbol.addRequirement(globalScope.getConstructor(type.getName()));
        return type.type();
    }

    @Override
    public List<PlExpression> visitNamedTypeParameters(IoMarkupParser.NamedTypeParametersContext ctx) {
        var plExpressionVisitor = new PlExpressionVisitor(scope());
        return ctx.plExpression().stream().map(plExpressionVisitor::visitPlExpression).toList();
    }

    @Override
    public ArrayParameters visitArrayParameters(IoMarkupParser.ArrayParametersContext ctx) {
        String iterationVarName = ctx.arrayIterationRange().NAME().getText();
        if (scope().containsName(iterationVarName))
            throw new RuntimeException();

        var plExpressionVisitor = new PlExpressionVisitor(scope());

        var startIterationExpression =
                plExpressionVisitor.visitPlExpression(ctx.arrayIterationRange().plExpression(0));
        var stopIterationExpression =
                plExpressionVisitor.visitPlExpression(ctx.arrayIterationRange().plExpression(1));

        var iterationVariable = new Variable(iterationVarName, PrimitiveType.INT32);
        scope().pushVariable(iterationVariable);
        return new ArrayParameters(
                iterationVariable,
                startIterationExpression,
                stopIterationExpression,
                visitSepValue(ctx.sepValue())
        );
    }

    @Override
    public List<ArrayParameters.IoSeparator> visitSepValue(IoMarkupParser.SepValueContext ctx) {
        String text;
        if (ctx.STRING() != null)
            text = ctx.STRING().getText();
        else if (ctx.CHAR() != null)
            text = ctx.CHAR().getText();
        else
            throw new RuntimeException(); // todo

        if (text.length() < 3)
            throw new RuntimeException(); // todo

        var result = text.substring(1, text.length() - 1).replace("\\n", "\n").chars().mapToObj(
                c -> switch (c) {
                    case '\n' -> ArrayParameters.IoSeparator.EOLN;
                    case ' ' -> ArrayParameters.IoSeparator.SPACE;
                    default -> throw new RuntimeException(); // todo
                }
        ).toList();

        if (result.isEmpty())
            throw new RuntimeException(); // todo

        return result;
    }

    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
