package org.gravitytwog.parser.nodes;

public abstract class Node {
    protected String content;

    public Node(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
