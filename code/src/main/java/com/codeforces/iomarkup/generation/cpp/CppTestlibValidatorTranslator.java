package com.codeforces.iomarkup.generation.cpp;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.pl.PlVarBinding;
import com.codeforces.iomarkup.symbol.ConstructorArgument;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.resolve.*;
import com.codeforces.iomarkup.symbol.scope.Scope;
import com.codeforces.iomarkup.type.*;

import java.util.*;
import java.util.stream.Collectors;

class CppTestlibValidatorTranslator extends CppTargetTranslator {
    private final Scope globalScope;
    private final Collection<ConstructorWithBody> constructors;
    private final List<ConstructorWithBody> delayedConstructors;
    private final Set<String> curLocals;

    public CppTestlibValidatorTranslator(Scope globalScope, Collection<ConstructorWithBody> constructor) {
        this.globalScope = globalScope;
        this.constructors = constructor;
        this.delayedConstructors = new ArrayList<>();
        this.curLocals = new HashSet<>();
    }

    public List<String> translateToList() {
        List<String> prototypes = new ArrayList<>();
        for (ConstructorWithBody constructor : constructors) {
            prototypes.add(generateSignature(constructor) + ";");
        }

        List<String> result = new ArrayList<>();
        for (ConstructorWithBody constructor : constructors) {
            result.add(generateSignature(constructor));
            constructor.getArguments().stream().map(Symbol::getName).forEach(curLocals::add);
            result.add("{");
            incIndentLevel();
            result.addAll(indent(translateConstructor(constructor)));
            decIndentLevel();
            result.add("}");
            result.add("");
            curLocals.clear();
        }

        while (!delayedConstructors.isEmpty()) {
            List<ConstructorWithBody> delayedConstructorsCopy = new ArrayList<>(delayedConstructors);
            delayedConstructors.clear();
            for (ConstructorWithBody constructor : delayedConstructorsCopy) {
                prototypes.add(generateSignature(constructor) + ";");

                result.add(generateSignature(constructor));
                constructor.getArguments().stream().map(Symbol::getName).forEach(curLocals::add);
                result.add("{");
                incIndentLevel();
                result.addAll(indent(translateConstructor(constructor)));
                decIndentLevel();
                result.add("}");
                result.add("");
                curLocals.clear();
            }
        }

        prototypes.add("");
        prototypes.addAll(result);
        return prototypes;
    }

    private List<String> translateConstructorBody(String constructorName, List<ConstructorItem> constructorBody) {
        List<String> result = new ArrayList<>();

        boolean delayedSpace = false;
        for (int i = 0; i < constructorBody.size(); ++i) {
            var item = constructorBody.get(i);
            if (item instanceof Variable variable) {
                result.addAll(translateVariable(constructorName, variable));
            } else if (item instanceof ConstructorIfAlt ifAlt) {
                result.addAll(translateIfAlt(constructorName, ifAlt, delayedSpace));
                delayedSpace = false;
            }

            if (item instanceof IoModifier ioModifier) {
                result.add("inf.readEoln();");
            } else if (i + 1 < constructorBody.size()) {
                var nextItem = constructorBody.get(i + 1);
                if (!(nextItem instanceof IoModifier)) {
                    if (nextItem instanceof ConstructorIfAlt constructorIfAlt) {
                        if (constructorIfAlt.getFalseItems() == null || constructorIfAlt.getFalseItems().isEmpty()) {
                            delayedSpace = true;
                            continue;
                        }
                    }
                    result.add("inf.readSpace();");
                }
            }
        }
        return result;
    }

    private List<String> translateConstructor(ConstructorWithBody constructor) {
        List<String> result = new ArrayList<>();
        result.add(getTypeName(constructor.getName()) + " " + constructor.getName() + ";");
        result.addAll(translateConstructorBody(constructor.getName(), constructor.getBody()));
        result.add("return " + constructor.getName() + ";");
        return result;
    }

    private List<String> translateSimpleType(String varLocate, Type type, List<PlExpression> args) {
        List<String> result = new ArrayList<>();
        if (TypeCharacteristic.PREDEFINED.is(type)) {
            if (type instanceof PrimitiveType primitiveType) {

                var testlibSuffix = switch (primitiveType) {
                    case BOOL, UINT64 -> throw new RuntimeException();
                    case CHAR -> "Char";
                    case INT32 -> "Int";
                    case UINT32, INT64 -> "Long";
                    case FLOAT32, FLOAT64 -> "Double";
                };

                result.add("%s = inf.read%s();".formatted(varLocate, testlibSuffix));
            } else if (type instanceof StringType) {
                result.add("%s = inf.readToken();".formatted(varLocate));
            } else throw new AssertionError();
        } else if (TypeCharacteristic.STRUCT.is(type)) {
            result.add("%s = read_%s(%s);".formatted(
                    varLocate,
                    type.getName(),
                    args.stream().map(arg -> new CppPlExpressionTranslator(arg).translate()).collect(Collectors.joining(", "))
            ));
        }
        return result;
    }

    private List<String> translateSimpleType(String constructorName, Variable variable, Type type, List<PlExpression> args) {
        var result = translateSimpleType(constructorName + "." + variable.getName(), type, args);
        var constraint = variable.getConstraint();
        if (constraint != null) {
            Map<String, String> locals = curLocals.stream().collect(Collectors.toMap(x -> x, x -> x, (a, b) -> b));

            var s = new CppPlExpressionTranslator(constraint, constructorName, locals).translate();

            result.add("ensuref(%s, \"%s\");".formatted(
                    s,
                    escape(s)
            ));
        }
        return result;
    }

    private List<String> translateArray(String constructorName, Variable variable,
                                        List<ArrayParameters> arrayParameters, VariableDescription component) {
        List<String> result = new ArrayList<>();
        if (arrayParameters.size() != 1) throw new RuntimeException(); // todo
        ArrayParameters param = arrayParameters.get(0);
        result.add("for (%s %s = %s; %s <= %s; %s++)".formatted(
                getTypeName(((Type) param.getIterationVarName().getDescription()).getName()),
                param.getIterationVarName().getName(),
                new CppPlExpressionTranslator(param.getIterationStartExpression()).translate(),
                param.getIterationVarName().getName(),
                new CppPlExpressionTranslator(param.getIterationStopExpression()).translate(),
                param.getIterationVarName().getName()
        ));
        result.add("{");

        result.add(indent("if (%s != %s)".formatted(
                param.getIterationVarName().getName(),
                new CppPlExpressionTranslator(param.getIterationStartExpression()).translate()
        )));
        result.add(indent("{"));
        incIndentLevel();

        for (var ioSeparator : param.getSeparator()) {
            result.add(indent("inf.read%s();".formatted(
                    switch (ioSeparator) {
                        case EOLN -> "Eoln";
                        case SPACE -> "Space";
                    }
            )));
        }

        decIndentLevel();
        result.add(indent("}"));

        String typeName;
        List<PlExpression> args = List.of();
        Type type;
        if (component instanceof Type type1) {
            typeName = type1.getName();
            type = type1;
        } else if (component instanceof ParametrizedDescription parametrizedDescription) {
            typeName = parametrizedDescription.type().getName();
            args = parametrizedDescription.arguments();
            type = parametrizedDescription.type();
        } else throw new RuntimeException();

        result.add(indent(getTypeName(typeName) + " tmp;"));

        result.addAll(indent(translateSimpleType("tmp", type, args)));

        result.add(indent("%s.%s.push_back(tmp);".formatted(constructorName, variable.getName())));

        var constraint = variable.getConstraint();
        if (constraint != null) {
            Map<String, String> locals = new HashMap<>();
            locals.put(param.getIterationVarName().getName(), "((%s) - (%s))".formatted(
                    param.getIterationVarName().getName(),
                    new CppPlExpressionTranslator(param.getIterationStartExpression()).translate()
            ));
            curLocals.forEach(x -> locals.put(x, x));

            var s = new CppPlExpressionTranslator(constraint, constructorName, locals).translate();
            result.add(indent("ensuref(%s, \"%s\");".formatted(
                    s,
                    escape(s)
            )));
        }

        result.add("}");
        return result;
    }

    private List<String> translateVariable(String constructorName, Variable variable) {
        List<String> result = new ArrayList<>();
        if (variable.getDescription() instanceof Type type) {
            result.addAll(translateSimpleType(constructorName, variable, type, List.of()));
        } else if (variable.getDescription() instanceof ParametrizedDescription parametrizedDescription) {
            result.addAll(translateSimpleType(constructorName, variable, parametrizedDescription.type(),
                    parametrizedDescription.arguments()));
        } else if (variable.getDescription() instanceof NamedTypeArrayDescription arrayDescription) {
            result.addAll(translateArray(constructorName, variable, arrayDescription.getArrayParameters(),
                    arrayDescription.getComponent()));
        } else if (variable.getDescription() instanceof UnnamedStructArrayDescription arrayDescription) {
            var iterVar = arrayDescription.getArrayParameters().get(0).getIterationVarName();
            result.addAll(translateArray(constructorName, variable, arrayDescription.getArrayParameters(),
                    new ParametrizedDescription(new StructType(variable.getName(), false), List.of(
                            new PlVarBinding(iterVar, List.of())
                    ))));
            delayedConstructors.add(new ConstructorWithBody(variable.getName(), List.of(
                    new ConstructorArgument(
                            iterVar.getName(),
                            ((Type) iterVar.getDescription()).getName()
                    )
            ), arrayDescription.getComponent()));
        }

        return result;
    }

    private List<String> translateIfAlt(String constructorName, ConstructorIfAlt ifAlt, boolean delayedSpace) {
        List<String> result = new ArrayList<>();
        result.add("if (" + new CppPlExpressionTranslator(ifAlt.getConditionalExpression(), constructorName).translate() + ")");
        result.add("{");

        if (delayedSpace)
            result.add(indent("inf.readSpace();"));

        result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getTrueItems())));

        result.add("}");
        if (ifAlt.getFalseItems() != null && !ifAlt.getFalseItems().isEmpty()) {
            result.add("else");
            result.add("{");

            result.addAll(indent(translateConstructorBody(constructorName, ifAlt.getFalseItems())));

            result.add("}");
        }
        return result;
    }

    private String getTypeName(String name) {
        var result = predefinedTypes.get(name);
        return result == null ? name + "_t" : result;
    }

    private String generateSignature(ConstructorWithBody constructor) {
        return "%s read_%s(%s)".formatted(
                getTypeName(constructor.getName()),
                constructor.getName(),
                constructor.getArguments().stream().map(arg -> {
                    var type = globalScope.getNamedType(arg.getType()).type();
                    if (TypeCharacteristic.PREDEFINED.is(type))
                        return predefinedTypes.get(type.getName()) + " " + arg.getName();
                    else if (TypeCharacteristic.STRUCT.is(type))
                        return getTypeName(type.getName()) + " " + arg.getName();
                    else throw new AssertionError();
                }).collect(Collectors.joining(", "))
        );
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\"", "\\\"")
                .replace("\t", "\\t");
    }
}
