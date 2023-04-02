package com.codeforces.iomarkup.symbol.resolve;

import com.codeforces.iomarkup.symbol.Constructor;
import com.codeforces.iomarkup.symbol.ConstructorArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConstructorWithBody extends Constructor {
    private final List<ConstructorItem> body = new ArrayList<>();

    public ConstructorWithBody(String name) {
        super(name);
    }

    public ConstructorWithBody(String name, List<ConstructorArgument> arguments) {
        super(name, arguments);
    }

    public void addBodyItem(ConstructorItem item) {
        body.add(item);
    }

    public List<ConstructorItem> getBody() {
        return Collections.unmodifiableList(body);
    }

    @Override
    public String toString() {
        return "ConstructorWithBody{" +
                "name=" + getName() + ", " +
                "arguments=" + getArguments() + ", " +
                "body=" + body +
                '}';
    }
}
