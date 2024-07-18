package org.gravitytwog.parser;

import org.gravitytwog.parser.nodes.RequireStatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ModuleParser {
    protected RequireStatementParser requireStatementParser = new RequireStatementParser();

    public Module parse(String moduleName, BufferedReader input) throws IOException {
        ArrayList<String> dependencies = new ArrayList<>();

        String line = input.readLine();
        while (line != null) {
            RequireStatement requireStatement = requireStatementParser.parse(line);
            if (requireStatement != null) {
                dependencies.add(requireStatement.getModuleName());
            }

            line = input.readLine();
        }

        return new Module(moduleName, dependencies);
    }
}
