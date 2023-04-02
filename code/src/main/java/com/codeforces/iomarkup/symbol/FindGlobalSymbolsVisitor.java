package com.codeforces.iomarkup.symbol;

import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.antlr.IoMarkupParserBaseVisitor;
import com.codeforces.iomarkup.symbol.resolve.ConstructorWithBody;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.NamedType;
import com.codeforces.iomarkup.type.StructType;

import java.util.List;

public class FindGlobalSymbolsVisitor extends IoMarkupParserBaseVisitor<Object> {
    private static final List<String> requiredGlobalSymbolNames = List.of("input", "output");

    @Override
    public Scope visitIoMarkup(IoMarkupParser.IoMarkupContext ctx) {
        var globalScope = new Scope();
        for (var namedStructCtx : ctx.namedStruct()) {
            var constructor = visitNamedStruct(namedStructCtx);
            String name = constructor.getName();
            if (globalScope.containsName(name))
                throw new RuntimeException(); // TODO: 26.03.2023

            globalScope.pushConstructor(constructor);
            globalScope.pushType(new NamedType(new StructType(name, !constructor.getArguments().isEmpty())));
        }


        for (String requiredGlobalSymbolName : requiredGlobalSymbolNames) {
            if (!globalScope.containsConstructor(requiredGlobalSymbolName))
                throw new RuntimeException(); // TODO: 30.03.2023
        }

        globalScope.getConstructor("output").addRequirement(globalScope.getConstructor("input"));

        return globalScope;
    }

    @Override
    public ConstructorWithBody visitNamedStruct(IoMarkupParser.NamedStructContext ctx) {
        var name = ctx.NAME().getText();
        var paramsCtx = ctx.namedStructParameters();
        if (paramsCtx == null)
            return new ConstructorWithBody(name);
        return new ConstructorWithBody(name, visitNamedStructParameters(paramsCtx));
    }

    @Override
    public List<ConstructorArgument> visitNamedStructParameters(IoMarkupParser.NamedStructParametersContext ctx) {
        return ctx.parameterDeclaration().stream().map(this::visitParameterDeclaration).toList();
    }

    @Override
    public ConstructorArgument visitParameterDeclaration(IoMarkupParser.ParameterDeclarationContext ctx) {
        return new ConstructorArgument(ctx.NAME().getText(), visitNamedType(ctx.namedType()));
    }

    @Override
    public String visitNamedType(IoMarkupParser.NamedTypeContext ctx) {
        return ctx.NAME().getText();
    }
}
