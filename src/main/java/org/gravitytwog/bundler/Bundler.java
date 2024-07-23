package org.gravitytwog.bundler;

import org.gravitytwog.bundler.sorters.GraphSorter;
import org.gravitytwog.parser.Module;
import org.gravitytwog.parser.ModuleParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Bundler {
    protected String rootPath;

    protected List<Module> modules;

    protected GraphSorter sorter;

    public Bundler(String rootPath, GraphSorter sorter) {
        this.rootPath = rootPath;
        this.modules = new ArrayList<>();
        this.sorter = sorter;
    }

    public void loadModules() throws Exception {
        this.modules.clear();

        ModuleParser moduleParser = new ModuleParser();

        Path rootPath = Paths.get(this.rootPath);
        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        FileReader fileReader = new FileReader(filePath.toFile());
                        BufferedReader reader = new BufferedReader(fileReader);

                        Module module = moduleParser.parse(rootPath.relativize(filePath).toString(), reader);
                        this.modules.add(module);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            this.modules = this.sorter.sort(this.modules);
        }
    }

    public void bundle(String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            for (var module : this.modules) {
                this.writeModule(module, writer);
            }
        }
    }

    private void writeModule(Module module, BufferedWriter writer) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(rootPath).resolve(module.getName()))) {
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void listModules() {
        for (Module module : this.modules) {
            System.out.print(module.getName() + " => ");
            for (String dependencyName : module.getDependencies()) {
                System.out.print(dependencyName + ", ");
            }
            System.out.println();
        }
    }
}
