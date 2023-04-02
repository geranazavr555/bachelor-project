parser grammar IoMarkupParser;
import IoMarkupProgLangParser;

@header {
package com.codeforces.iomarkup.antlr;
}

options {
    tokenVocab = IoMarkupLexer;
}

ioMarkup: (namedStruct)* EOF;

namedStruct: NAME namedStructParameters? struct;

namedStructParameters: LPAR (parameterDeclaration (COMMA parameterDeclaration)*)? RPAR;

parameterDeclaration: NAME COLON namedType;

struct: LBRACE structItem* RBRACE;

structItem: conditionalAlternative | variableDeclaration | ioModifier;

conditionalAlternative: IF LPAR plExpression RPAR struct (ELSE struct)?;

ioModifier: EOLN_MODIFIER SEMICOLON;

variableDeclaration: NAME COLON variableType variableConstraint? SEMICOLON;

variableType: arrayOfUnnamedStruct | (namedType namedTypeParameters? arrayParameters*);

arrayOfUnnamedStruct: ARRAY arrayParameters+ OF struct;

variableConstraint: PIPE plExpression;

namedType: NAME;

namedTypeParameters: LPAR (plExpression (COMMA plExpression)*)? RPAR;

arrayParameters: LBRACKET arrayIterationRange (COMMA SEP ASSIGN sepValue) RBRACKET;

arrayIterationRange: NAME ASSIGN plExpression DOT DOT plExpression;

sepValue: STRING | CHAR;
