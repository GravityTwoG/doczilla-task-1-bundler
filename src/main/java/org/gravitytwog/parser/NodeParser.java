package org.gravitytwog.parser;

import org.gravitytwog.parser.nodes.Node;

public interface NodeParser {
    Node parse(String line);
}
