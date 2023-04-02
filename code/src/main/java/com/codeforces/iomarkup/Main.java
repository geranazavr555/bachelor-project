package com.codeforces.iomarkup;

import com.codeforces.iomarkup.antlr.IoMarkupLexer;
import com.codeforces.iomarkup.antlr.IoMarkupParser;
import com.codeforces.iomarkup.generation.impl.cpp.CppTranslator;
import com.codeforces.iomarkup.symbol.FindGlobalSymbolsVisitor;
import com.codeforces.iomarkup.symbol.Symbol;
import com.codeforces.iomarkup.symbol.resolve.ConstructorWithBody;
import com.codeforces.iomarkup.symbol.resolve.ResolveSymbolsVisitor;
import com.codeforces.iomarkup.symbol.scope.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Main {
    private Set<String> generatedStructs = new HashSet<>();

    private void generate(ConstructorWithBody constructor) {
        if (!generatedStructs.contains(constructor.getName()))
            generateImpl(constructor);
    }

    private void generateImpl(ConstructorWithBody constructor) {
        generatedStructs.add(constructor.getName());
        for (Symbol symbol : constructor.getRequirements()) {
            if (symbol instanceof ConstructorWithBody requiredConstructor
                    && !generatedStructs.contains(requiredConstructor.getName())) {
                generateImpl(requiredConstructor);
            }
        }

        var kek = new CppTranslator();
        kek.translateToStructDeclaration(constructor);
        kek.getStructs().forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException {
        var lexer = new IoMarkupLexer(CharStreams.fromPath(Path.of(
                "C:\\Programing\\bachelor-thesis\\materials-repo\\examples-2-wo-constraints\\1772F.txt"
        )));
        var parser = new IoMarkupParser(new CommonTokenStream(lexer));
        var root = parser.ioMarkup();
        Scope globalScope = new FindGlobalSymbolsVisitor().visitIoMarkup(root);
        globalScope = new ResolveSymbolsVisitor(globalScope).visitIoMarkup(root);

        var kek = new Main();
        globalScope.getConstructors().forEach(kek::generate);

//        for (String s : new String[]{"picture", "input", "output"}) {
//            var kek = new CppTranslator();
//            kek.translateToStructDeclaration(globalScope.getConstructor(s));
//            kek.getStructs().forEach(System.out::println);
//        }
    }
}
