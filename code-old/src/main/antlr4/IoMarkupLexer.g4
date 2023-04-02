lexer grammar IoMarkupLexer;

@header {
package com.codeforces.iomarkup.antlr;
}

LINE_COMMENT: '//' .*? '\r'? ('\n' | EOF) -> skip;
COMMENT: '/*' .*? '*/' -> skip;

TRUE: 'true';
FALSE: 'false';

LOGICAL_AND: '&&';
LOGICAL_OR: '||';
LOGICAL_NOT: '!';
BITWISE_AND: '&';
BITWISE_XOR: '^';
BITWISE_OR: '|';
BITWISE_NOT: '~';
EQUALS: '==';
NOT_EQUALS: '!=';
LESS_EQUAL: '<=';
GREATER_EQUAL: '>=';
PLUS: '+';
MINUS: '-';
MULTIPLICATION: '*';
DIVISION: '/';
MODULO: '%';


COLON: ':';
QUESTION: '?';
ASSIGN: '=';
DOT: '.';
QUOTE: '\'';
DOUBLE_QUOTE: '"';
COMMA: ',';
SEMICOLON: ';';
LESS: '<';
GREATER: '>';
LBRACE: '{';
RBRACE: '}';
LBRACKET: '[';
RBRACKET: ']';
LPAR: '(';
RPAR: ')';

EOLN_MODIFIER: 'eoln';
STRUCT: 'struct';
ENUM: 'enum';
MAPS: 'maps';
MAPPER: 'mapper';
IF: 'if';
ITER: 'iter';

EXPONENT: 'e' | 'E';
DIGITS: [0-9'_]+;
NAME: [a-zA-Z_][a-zA-Z0-9_]*;
WS: [ \t\r\n]+ -> skip;

CHAR: '\'' [^'] '\''; // See also PlExpressionVisitor#visitPlCharValue(IoMarkupParser.PlCharValueContext)
STRING: '"' [^"]* '"'; // See also PlExpressionVisitor#visitPlStringValue(IoMarkupParser.PlStringValueContext)