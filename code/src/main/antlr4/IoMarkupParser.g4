parser grammar IoMarkupParser;
import IoMarkupProgLangParser;

@header {
package com.codeforces.iomarkup.antlr;
}

options {
    tokenVocab = IoMarkupLexer;
}

ioMarkup: (struct | enum | mapper)* EOF;

mapper: MAPPER typeArgument MAPS varType NAME LPAR NAME RPAR LBRACE mapperBody RBRACE;

mapperBody: ; // Not implemented yet todo

typeArgument: NAME typeArgumentParams? typeArgumentArrayParam?;

typeArgumentParams: LESS NAME (COMMA NAME)* GREATER;

typeArgumentArrayParam: LBRACKET NAME RBRACKET;

enum: ENUM NAME MAPS varType LBRACE enumItem* RBRACE;

enumItem: NAME ASSIGN plExpression SEMICOLON;

struct: STRUCT NAME LBRACE structItem* RBRACE;

structItem: (varDeclaration | ioModifier) SEMICOLON;

ioModifier: EOLN_MODIFIER;

varDeclaration: varType varMapper? NAME condition?;

varMapper: MAPPER NAME;

varType: NAME typeParams? arrayParam?;

typeParams: LESS plExpression (COMMA plExpression)* GREATER;

arrayParam: LBRACKET plExpression RBRACKET (ITER NAME)?;

condition: IF plExpression;