package org.gravitytwog;

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
    // outbound dependencies of module
    private final HashMap<Module, Set<Module>> outbound;
    // inbound dependents of module
    private final Map<Module, Set<Module>> inbound;

    public Bundler(String rootPath) {
        this.rootPath = rootPath;
        this.modules = new ArrayList<>();
        this.outbound = new HashMap<>();
        this.inbound = new HashMap<>();
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

            this.sortModules();
        }
    }

    // Kahn's Algorithm
    private void sortModules() throws Exception {
        this.buildDependencyGraphs();

        List<Module> result = new ArrayList<>();

        while (!this.inbound.isEmpty()) {
            boolean independentModuleFound = false;

            for (Map.Entry<Module, Set<Module>> entry : this.inbound.entrySet()) {
                var dependents = entry.getValue();
                if (dependents.isEmpty()) {
                    var module = entry.getKey();
                    // add module without dependents
                    result.add(module);

                    // remove this module from inbound dependents graph
                    this.inbound.remove(module);
                    for (Module dependency : this.outbound.get(module)) {
                        this.inbound.get(dependency).remove(module);
                    }

                    independentModuleFound = true;

                    // Should break to avoid ConcurrentModificationException
                    break;
                }
            }

            if (!independentModuleFound) {
                List<String> moduleNames = new ArrayList<>();
                this.inbound.keySet().forEach(module -> moduleNames.add(module.getName() + ", "));
                throw new Exception("Cycle found: " + moduleNames);
            }
        }

        this.modules = result.reversed();
    }

    private void buildDependencyGraphs() {
        Map<String, Module> map = new HashMap<>();
        this.outbound.clear();
        this.inbound.clear();

        for (Module module : this.modules) {
            map.put(module.getName(), module);
            outbound.put(module, new HashSet<>());
            inbound.put(module, new HashSet<>());
        }

        for (Module dependent : this.modules) {
            Set<Module> dependencies = outbound.get(dependent);

            // add dependencies of module to outbound dependencies
            for (String dependencyName : dependent.getDependencies()) {
                Module dependency = map.get(dependencyName);
                dependencies.add(dependency);
            }

            // add module to inbound dependents
            for (Module dependency : dependencies) {
                var dependents = inbound.get(dependency);
                dependents.add(dependent);
            }
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
