package com.codeforces.iomarkup.generation.python;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.generation.FilesTranslator;
import com.codeforces.iomarkup.generation.TranslatedFile;
import com.codeforces.iomarkup.symbol.scope.Scope;

import java.util.Collection;
import java.util.List;

public class PythonSolutionTranslator implements FilesTranslator {
    private static final String TEMPLATE = """
            def iomarkup_div(x, y):
                return x / y if isinstance(x, float) or isinstance(y, float) else x // y
            
            
            class Scanner:
                def __init__(self, next_line_getter):
                    self._next_line_getter = next_line_getter
                    self._tokens = []
                    self._i = 0
                    self._j = None
            
                def _ensure_token(self):
                    if self._i >= len(self._tokens):
                        self._tokens.extend(self._next_line_getter().strip().split())
            
                def next(self):
                    self._ensure_token()
                    next_token = self._tokens[self._i]
                    if self._j is not None:
                        next_token = next_token[self._j:]
                    self._i += 1
                    self._j = None
                    return next_token
            
                def next_int(self):
                    return int(self.next())
            
                def next_float(self):
                    return float(self.next())
            
                def next_char(self):
                    self._ensure_token()
                    next_token = self._tokens[self._i]
                    if self._j is not None:
                        if self._j >= len(next_token):
                            self._i += 1
                            self._j = None
                            return self.next_char()
                        next_token = next_token[self._j]
                        self._j += 1
                    else:
                        self._j = 1
                        next_token = next_token[0]
                    return next_token
            
                def next_bool(self):
                    return self.next() == "true"
            
                @staticmethod
                def from_input():
                    return Scanner(input)
            
            
            _scanner = Scanner.from_input()
            
            # Structure declarations
            %s
            
            input = None
            
            # Input functions
            %s
            
            # Output functions
            %s
            
            def solve(input):
                # Write your code here. This function should return an Output object.
                pass
            
            if __name__ == '__main__':
                input = read_input()
                write_output(solve(input))
            
            """;

    private final PythonStructDeclarationsTranslator pythonStructDeclarationsTranslator;
    private final PythonGraderReadTranslator pythonGraderReadTranslator;
    private final PythonGraderWriteTranslator pythonGraderWriteTranslator;

    public PythonSolutionTranslator(Scope globalScope) {
        pythonStructDeclarationsTranslator = new PythonStructDeclarationsTranslator(globalScope);
        pythonGraderReadTranslator = new PythonGraderReadTranslator(globalScope, globalScope.getConstructors());
        pythonGraderWriteTranslator = new PythonGraderWriteTranslator(globalScope, globalScope.getConstructors());
    }

    @Override
    public Collection<TranslatedFile> translate() {
        return List.of(
                new TranslatedFile(
                        TargetComponent.VALIDATOR,
                        "solution.py",
                        TEMPLATE.formatted(
                                pythonStructDeclarationsTranslator.translate(),
                                pythonGraderReadTranslator.translate(),
                                pythonGraderWriteTranslator.translate()
                        )
                )
        );
    }
}
