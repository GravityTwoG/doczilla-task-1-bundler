package org.gravitytwog.parser;

import org.gravitytwog.parser.nodes.RequireStatement;

public class RequireStatementParser implements NodeParser {
    @Override
    public RequireStatement parse(String line) {
        if (!line.startsWith("require")) {
            return null;
        }

        String rest = line.substring(8).trim();

        return new RequireStatement(line, rest.substring(1, rest.length() - 1));
    }
}
