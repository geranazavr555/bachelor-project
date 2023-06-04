package com.codeforces.iomarkup.pl;

import com.codeforces.iomarkup.symbol.resolve.ArrayDescription;
import com.codeforces.iomarkup.symbol.resolve.ParametrizedDescription;
import com.codeforces.iomarkup.symbol.resolve.Variable;
import com.codeforces.iomarkup.symbol.resolve.VariableDescription;
import com.codeforces.iomarkup.type.ArrayType;
import com.codeforces.iomarkup.type.StructType;
import com.codeforces.iomarkup.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PlVarBinding extends PlExpression {
    private final Variable variable;
    private final List<PlFieldLocate> fieldLocate;

    @Override
    public Type getType() {
       return descriptionToType(variable.getDescription());
    }

    private static Type descriptionToType(VariableDescription description) {
        if (description instanceof Type type)
            return type;

        if (description instanceof ArrayDescription<?> arrayDescription) {
            Object component = arrayDescription.getComponent();
            Type resultType;
            if (component instanceof VariableDescription componentDescription)
                resultType = descriptionToType(componentDescription);
            else if (component instanceof List<?>)
                resultType = new StructType();
            else throw new AssertionError();

            var arrayParameters = arrayDescription.getArrayParameters();
            for (int i = arrayParameters.size() - 1; i >= 0; i--) {
                var stop = arrayParameters.get(i).getIterationStopExpression();
                var start = arrayParameters.get(i).getIterationStartExpression();
                var lengthExpr = new PlBinaryOperator(PlBinaryOperator.Op.SUBTRACTION, stop, start);
                lengthExpr.ensureType();
                resultType = new ArrayType(resultType, lengthExpr);
            }

            return resultType;
        }

        if (description instanceof ParametrizedDescription parametrizedDescription)
            return parametrizedDescription.type();

        throw new AssertionError();
    }
}
