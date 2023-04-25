package com.codeforces.iomarkup.generation;

import com.codeforces.iomarkup.exec.TargetComponent;

public record TranslatedFile(TargetComponent component, String name, String content) {
}
