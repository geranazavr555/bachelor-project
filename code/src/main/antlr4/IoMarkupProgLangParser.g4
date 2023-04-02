parser grammar IoMarkupProgLangParser;

options {
    tokenVocab = IoMarkupLexer;
}

// From the smallest to the highest priority
// plExpression is a rvalue in terms of C++
plExpression: plImplLogicalOr (QUESTION plImplLogicalOr COLON plImplLogicalOr)?;

plImplLogicalOr: plImplLogicalAnd (LOGICAL_OR plImplLogicalAnd)*;
plImplLogicalAnd: plImplBitwiseOr (LOGICAL_AND plImplBitwiseOr)*;
plImplBitwiseOr: plImplBitwiseXor (PIPE plImplBitwiseXor)*;
plImplBitwiseXor: plImplBitwiseAnd (BITWISE_XOR plImplBitwiseAnd)*;
plImplBitwiseAnd: plImplEquality (BITWISE_AND plImplEquality)*;
plImplEquality: plImplRelational (plImplEqualityOp plImplRelational)*;
plImplRelational: plImplBitwiseShift (plImplRelationalOp plImplBitwiseShift)?;
plImplBitwiseShift: plImplAdditiveBinary (plImplBitwiseShiftOp plImplAdditiveBinary)?;
plImplAdditiveBinary: plImplMultiplicativeBinary (plImplAdditiveOp plImplMultiplicativeBinary)*;
plImplMultiplicativeBinary: plImplPowBinary (plImplMultiplicativeBinaryOp plImplPowBinary)*;
plImplPowBinary: plImplPrefixUnary (POW plImplPrefixUnary)*;
plImplPrefixUnary: plImplPrefixUnaryOp* plImplHighestPriority;

plImplHighestPriority: ((LPAR plExpression RPAR) | plFunctionCall | plValue | plVarBinding) plSubscript*;

plImplEqualityOp: EQUALS | NOT_EQUALS;
plImplRelationalOp: GREATER | LESS | GREATER_EQUAL | LESS_EQUAL;
plImplAdditiveOp: PLUS | MINUS;
plImplMultiplicativeBinaryOp: MULTIPLICATION | DIVISION | MODULO;
plImplPrefixUnaryOp: LOGICAL_NOT | BITWISE_NOT | MINUS;
plImplBitwiseShiftOp: LESS LESS | GREATER GREATER;

plFunctionCall: NAME LPAR plImplFunctionArgs? RPAR;
plImplFunctionArgs: plExpression (COMMA plExpression)*;

plVarBinding: NAME;

plSubscript: LBRACKET plExpression RBRACKET;

plValue: plNumValue | plBoolValue | plCharValue | plStringValue;
plBoolValue: TRUE | FALSE;
plCharValue: CHAR;
plStringValue: STRING;
plNumValue: NUM_VALUE;