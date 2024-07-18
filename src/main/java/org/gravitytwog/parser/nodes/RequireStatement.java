package org.gravitytwog.parser.nodes;

public class RequireStatement extends Node {
    protected String modulePath;

    public RequireStatement(String content, String modulePath) {
        super(content);
        this.modulePath = modulePath;
    }

    public String getModulePath() {
        return modulePath;
    }
}
