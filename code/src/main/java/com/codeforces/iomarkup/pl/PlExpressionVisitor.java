package com.codeforces.iomarkup.pl;

public interface PlExpressionVisitor<T1, T2, T3, T4, T5, T6, T7> {
    T1 visitBinaryOperator(PlBinaryOperator plBinaryOperator);

    T2 visitFunctionCall(PlFunctionCall plFunctionCall);

    T3 visitIfOperator(PlIfOperator plIfOperator);

    T4 visitSubscript(PlSubscript plSubscript);

    T5 visitUnaryOperator(PlUnaryOperator plUnaryOperator);

    <T> T6 visitValue(PlValue<T> value);

    T7 visitVarBinding(PlVarBinding plVarBinding);
}
