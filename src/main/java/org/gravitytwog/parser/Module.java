package org.gravitytwog.parser;

import org.gravitytwog.parser.nodes.Node;
import org.gravitytwog.parser.nodes.RequireStatement;

import java.util.ArrayList;

public class Module {
    protected String name;
    protected ArrayList<Node> nodes;

    public Module(String name, ArrayList<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<RequireStatement> getRequires() {
        return new ArrayList<>(this.nodes.stream()
                .filter(RequireStatement.class::isInstance)
                .map(RequireStatement.class::cast)
                .toList());
    }
}
