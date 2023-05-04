package com.codeforces.iomarkup;

import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        var ioMarkup = new IoMarkup(Path.of("C:\\Programing\\bachelor-thesis\\code-repo\\code\\src\\test\\resources\\complexTestMarkups\\1823D.txt"));
        var files = ioMarkup.generate(TargetComponent.VALIDATOR, TargetLanguage.CPP);
        for (TranslatedFile file : files) {
            System.out.println(file.content());
        }
    }
}
