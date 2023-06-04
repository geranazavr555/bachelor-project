package com.codeforces.iomarkup;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        var ioMarkup = new IoMarkup(Path.of("C:\\Programing\\bachelor-thesis\\code-repo\\code\\src\\test\\resources\\complexTestMarkups\\1772F.txt"));
        var ioMarkup = new IoMarkup("""
                case {
                    n: int32;
                }

                input {
                    a: int32 | -1000 <= a && a <= 1000;
                    b: int32 | -1000 <= b && b <= 1000;
                }

                output {
                    sum: int32 | sum <= true;
                }

                """);
        var files = ioMarkup.generate(TargetComponent.CHECKER, TargetLanguage.CPP);
        for (TranslatedFile file : files) {
            System.out.println(file.content());
        }
    }
}
