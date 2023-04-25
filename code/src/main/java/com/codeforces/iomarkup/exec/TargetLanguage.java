package com.codeforces.iomarkup.exec;

import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.cpp.CppCheckerTranslator;
import com.codeforces.iomarkup.generation.cpp.CppSolutionTranslator;
import com.codeforces.iomarkup.generation.cpp.CppValidatorTranslator;
import com.codeforces.iomarkup.generation.java.JavaSolutionTranslator;
import com.codeforces.iomarkup.generation.python.PythonSolutionTranslator;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.Map;
import java.util.function.Function;

public enum TargetLanguage {
    CPP(Map.of(
            TargetComponent.VALIDATOR, CppValidatorTranslator::new,
            TargetComponent.CHECKER, CppCheckerTranslator::new,
            TargetComponent.SOLUTION, CppSolutionTranslator::new
    )),
    JAVA(Map.of(
            TargetComponent.SOLUTION, JavaSolutionTranslator::new
    )),
    PYTHON(Map.of(
            TargetComponent.SOLUTION, PythonSolutionTranslator::new
    ));

    private final Map<TargetComponent, Function<Scope, FilesTranslator>> componentToTranslator;

    TargetLanguage(Map<TargetComponent, Function<Scope, FilesTranslator>> componentToTranslator) {
        this.componentToTranslator = componentToTranslator;
    }

    public FilesTranslator getTranslator(TargetComponent targetComponent, Scope globalScope) {
        var translatorConstructor = componentToTranslator.get(targetComponent);
        if (translatorConstructor == null)
            throw new IllegalArgumentException();

        return translatorConstructor.apply(globalScope);
    }
}
