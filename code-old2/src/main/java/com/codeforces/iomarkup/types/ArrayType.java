package com.codeforces.iomarkup.types;

import com.codeforces.iomarkup.pl.PlExpression;
import com.codeforces.iomarkup.symbols.NeedResolveSymbols;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.codeforces.iomarkup.types.TypeCharacteristic.*;

public class ArrayType implements Type, NeedResolveSymbols {
    @Nullable
    private final PlExpression lengthExpression;

    private Type componentType;

    protected final Set<TypeCharacteristic> characteristics;

    public ArrayType(Type componentType) {
        this(componentType, null);
    }

    public ArrayType(Type componentType, @Nullable PlExpression lengthExpression) {
        this.componentType = componentType;
        this.lengthExpression = lengthExpression;
        this.characteristics = EnumSet.of(PREDEFINED, ARRAY);
        if (lengthExpression != null) {
            this.characteristics.add(PARAMETRIZED);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<TypeCharacteristic> getCharacteristics() {
        return Collections.unmodifiableSet(characteristics);
    }

    @Override
    public List<PlExpression> getParameterExpressions() {
        return lengthExpression == null ? List.of() : List.of(lengthExpression);
    }

    @Nullable
    public PlExpression getLengthExpression() {
        return lengthExpression;
    }

    public Type getComponentType() {
        return componentType;
    }
}
