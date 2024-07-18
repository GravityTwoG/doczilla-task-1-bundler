package org.gravitytwog;

import org.gravitytwog.parser.Module;
import org.gravitytwog.parser.ModuleParser;
import org.gravitytwog.parser.nodes.Node;
import org.gravitytwog.parser.nodes.RequireStatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws IOException {
        ModuleParser moduleParser = new ModuleParser();

        StringReader stringReader = new StringReader("""
Lorem ipsum dolor sit amet, consectetur adipiscing elit.
require 'Folder 2/File 2-1'
Praesent feugiat egestas sem, id luctus lectus dignissim ac.
        """);
        BufferedReader reader = new BufferedReader(stringReader);

        Module module = moduleParser.parse("File 1-1", reader);

        for (Node node : module.getNodes()) {
            if (node instanceof RequireStatement) {
                System.out.println(((RequireStatement) node).getModulePath());
            }
        }
    }
}