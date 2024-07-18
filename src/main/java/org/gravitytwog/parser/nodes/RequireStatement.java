package org.gravitytwog.parser.nodes;

public class RequireStatement extends Node {
    protected String moduleName;

    public RequireStatement(String content, String moduleName) {
        super(content);
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
}
