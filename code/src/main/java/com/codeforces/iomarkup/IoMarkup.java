package com.codeforces.iomarkup;

import com.codeforces.iomarkup.antlr.IoMarkupLexer;
import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.FindGlobalSymbolsVisitor;
import com.codeforces.iomarkup.symbol.resolve.ResolveSymbolsVisitor;
import com.codeforces.iomarkup.symbol.scope.Scope;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Collection;

public class IoMarkup {
    private Scope globalScope;

    public IoMarkup(String ioMarkup) {
        this(CharStreams.fromString(ioMarkup));
    }

    public IoMarkup(String ioMarkup, String sourceFileName) {
        this(CharStreams.fromString(ioMarkup, sourceFileName));
    }

    public IoMarkup(Path path) throws IOException {
        this(CharStreams.fromPath(path));
    }

    public IoMarkup(Reader reader) throws IOException {
        this(CharStreams.fromReader(reader));
    }

    private IoMarkup(CharStream antlrCharStream) {
        var lexer = new IoMarkupLexer(antlrCharStream);
        var parser = new IoMarkupParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        var parseTreeRoot = parser.ioMarkup();
        globalScope = new FindGlobalSymbolsVisitor().visitIoMarkup(parseTreeRoot);
        globalScope = new ResolveSymbolsVisitor(globalScope).visitIoMarkup(parseTreeRoot);
    }

    public Collection<TranslatedFile> generate(TargetComponent targetComponent, TargetLanguage targetLanguage) {
        return targetLanguage.getTranslator(targetComponent, globalScope).translate();
    }
}
