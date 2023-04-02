lexer grammar IoMarkupLexer;

@header {
package com.codeforces.iomarkup.antlr;
}

LINE_COMMENT: '#' .*? '\r'? ('\n' | EOF) -> skip;
COMMENT: '/*' .*? '*/' -> skip;

fragment ESCAPED_CHAR: '\\n';

CHAR: '\'' ((~['\\\r\n]) | ESCAPED_CHAR) '\'';
STRING: '"' ((~["\\\r\n]) | ESCAPED_CHAR)* '"';

TRUE: 'true';
FALSE: 'false';

LOGICAL_AND: '&&';
LOGICAL_OR: '||';
LOGICAL_NOT: '!';
BITWISE_AND: '&';
BITWISE_XOR: '^';
PIPE: '|';
BITWISE_NOT: '~';
EQUALS: '==';
NOT_EQUALS: '!=';
LESS_EQUAL: '<=';
GREATER_EQUAL: '>=';
PLUS: '+';
MINUS: '-';
POW: '**';
MULTIPLICATION: '*';
DIVISION: '/';
MODULO: '%';
DOT: '.';


COLON: ':';
QUESTION: '?';
ASSIGN: '=';
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

SEP: 'sep';
EOLN_MODIFIER: 'eoln';
STRUCT: 'struct';
ENUM: 'enum';
MAPS: 'maps';
MAPPER: 'mapper';
IF: 'if';
ITER: 'iter';
ARRAY: 'array';
OF: 'of';
ELSE: 'else';

fragment U_NUM_LITERAL_MODIFIER: 'u' | 'U';
fragment L_NUM_LITERAL_MODIFIER: 'l' | 'L';
fragment F_NUM_LITERAL_MODIFIER: 'f' | 'F';
fragment EXPONENT: 'e' | 'E';
fragment DIGITS: [0-9][0-9'_]*;

fragment INTEGER_SUFFIX: U_NUM_LITERAL_MODIFIER? L_NUM_LITERAL_MODIFIER?;
fragment FLOAT_SUFFIX: ('.' DIGITS)? (EXPONENT DIGITS)? F_NUM_LITERAL_MODIFIER?;

NUM_VALUE: DIGITS INTEGER_SUFFIX FLOAT_SUFFIX;

NAME: [a-zA-Z_][a-zA-Z0-9_]*;
WS: [ \t\r\n]+ -> skip;