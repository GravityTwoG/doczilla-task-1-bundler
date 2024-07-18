package org.gravitytwog.parser;

import java.util.ArrayList;

public class Module {
    protected String name;
    protected ArrayList<String> dependencies;

    public Module(String name, ArrayList<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDependencies() {
        return dependencies;
    }
}
