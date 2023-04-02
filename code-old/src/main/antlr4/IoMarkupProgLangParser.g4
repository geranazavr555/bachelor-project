parser grammar IoMarkupProgLangParser;

options {
    tokenVocab = IoMarkupLexer;
}

// === BEGIN OPERATORS ===

// From the smallest to the highest priority
// plExpression is a rvalue in terms of C++
plExpression: plImplLogicalOr (QUESTION plExpression COLON plExpression)?;

plImplLogicalOr: plImplLogicalAnd (LOGICAL_OR plImplLogicalAnd)?;
plImplLogicalAnd: plImplBitwiseOr (LOGICAL_AND plImplBitwiseOr)?;
plImplBitwiseOr: plImplBitwiseXor (BITWISE_OR plImplBitwiseXor)?;
plImplBitwiseXor: plImplBitwiseAnd (BITWISE_XOR plImplBitwiseAnd)?;
plImplBitwiseAnd: plImplEquality (BITWISE_AND plImplEquality)?;
plImplEquality: plImplRelational (plImplEqualityOp plImplRelational)?;
plImplRelational: plImplBitwiseShift (plImplRelationalOp plImplBitwiseShift)?;
plImplBitwiseShift: plImplAdditiveBinary; //(bitwiseShiftOp plImplAdditiveBinaryOp)?;
plImplAdditiveBinary: plImplMultiplicativeBinary (plImplAdditiveOp plImplMultiplicativeBinary)?;
plImplMultiplicativeBinary: plImplPrefixUnary (plImplMultiplicativeBinaryOp plImplPrefixUnary)?;
plImplPrefixUnary: plImplPrefixUnaryOp? plImplHighestPriority;

plImplHighestPriority: ((LPAR plExpression RPAR) | plFunctionCall | plVarBinding | plValue) plSubscript?;

plFunctionCall: NAME LPAR plImplFunctionArgs? RPAR;
plImplFunctionArgs: plExpression (COMMA plExpression)*;

plVarBinding: NAME;

plSubscript: LBRACKET plExpression RBRACKET;

plImplEqualityOp: EQUALS | NOT_EQUALS;
plImplRelationalOp: GREATER | LESS | GREATER_EQUAL | LESS_EQUAL;
plImplAdditiveOp: PLUS | MINUS;
plImplMultiplicativeBinaryOp: MULTIPLICATION | DIVISION | MODULO;
//plImplIncDecOp: PLUS PLUS | MINUS MINUS;
plImplPrefixUnaryOp: /*plImplIncDecOp |*/ LOGICAL_NOT | BITWISE_NOT | MINUS;
//bitwiseShiftOp: LESS LESS | GREATER GREATER

// === END OPERATORS ===

plValue: plNumValue | plBoolValue | plCharValue | plStringValue;
plBoolValue: TRUE | FALSE;
plCharValue: CHAR;
plStringValue: STRING;
plNumValue: plIntValue (DOT DIGITS)? (EXPONENT plIntValue)?;
plIntValue: DIGITS;