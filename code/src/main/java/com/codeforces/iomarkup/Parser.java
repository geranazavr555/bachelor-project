package com.codeforces.iomarkup;

import com.codeforces.iomarkup.antlr.IoMarkupLexer;
import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.symbols.RawSymbolsVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Parser {
    public void parse(String ioMarkupString) {
        var lexer = new IoMarkupLexer(CharStreams.fromString(ioMarkupString));
        var parser = new IoMarkupParser(new CommonTokenStream(lexer));
        var root = parser.ioMarkup();
        var rawSymbolsVisitor = new RawSymbolsVisitor();
        var rawSymbols = rawSymbolsVisitor.visitIoMarkup(root);


    }
}
