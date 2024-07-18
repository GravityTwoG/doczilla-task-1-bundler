package org.gravitytwog;

import org.gravitytwog.parser.Module;
import org.gravitytwog.parser.ModuleParser;
import org.gravitytwog.parser.nodes.CommonLine;
import org.gravitytwog.parser.nodes.RequireStatement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Bundler {
    protected List<Module> modules = new ArrayList<>();

    public void loadModules(String root) throws Exception {
        this.modules.clear();

        ModuleParser moduleParser = new ModuleParser();

        Path rootPath = Paths.get(root);
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
        Map<Module, Set<Module>> outbound = this.buildOutboundDependenciesGraph();
        Map<Module, Set<Module>> inbound = this.buildInboundDependenciesGraph(outbound);

        List<Module> result = new ArrayList<>();

        while (!inbound.isEmpty()) {
            Boolean independentModuleFound = false;

            for (Map.Entry<Module, Set<Module>> entry : inbound.entrySet()) {
                var dependents = entry.getValue();
                if (dependents.isEmpty()) {
                    var dependency = entry.getKey();
                    // add module without dependents
                    result.add(dependency);

                    // remove this module from inbound dependencies graph
                    for (Module outboundDependency : outbound.get(dependency)) {
                        inbound.get(outboundDependency).remove(dependency);
                    }

                    inbound.remove(dependency);

                    independentModuleFound = true;

                    // Should break to avoid ConcurrentModificationException
                    break;
                }
            }

            if (!independentModuleFound) {
                List<String> moduleNames = new ArrayList<>();
                inbound.keySet().forEach(module -> moduleNames.add(module.getName() + ", "));
                throw new Exception("Cycle found: " + moduleNames);
            }
        }

        this.modules = result.reversed();
    }

    private Map<Module, Set<Module>> buildOutboundDependenciesGraph() {
        Map<String, Module> map = new HashMap<>();
        Map<Module, Set<Module>> graph = new HashMap<>();

        for (Module dependent : this.modules) {
            map.put(dependent.getName(), dependent);
            graph.put(dependent, new HashSet<>());
        }

        for (Module dependent : this.modules) {
            Set<Module> dependencies = graph.get(dependent);

            for (RequireStatement requireStatement : dependent.getRequires()) {
                Module dependency = map.get(requireStatement.getModulePath());
                dependencies.add(dependency);
            }
        }

        return graph;
    }

    private Map<Module, Set<Module>> buildInboundDependenciesGraph(Map<Module, Set<Module>> outbound) {
        Map<Module, Set<Module>> inbound = new HashMap<>();

        for (Module dependency : this.modules) {
            inbound.put(dependency, new HashSet<>());
        }

        for (Module dependent : this.modules) {
            Set<Module> dependencies = outbound.get(dependent);

            for (Module dependency : dependencies) {
                var dependents = inbound.get(dependency);
                dependents.add(dependent);
            }
        }
        return inbound;
    }

    public void bundle(String path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            for (var module : this.modules) {
                this.writeModule(module, writer);
            }
        }
    }

    private void writeModule(Module module, BufferedWriter writer) throws IOException {
        for (var node : module.getNodes()) {
            if (node instanceof CommonLine) {
                writer.write(node.getContent());
                writer.newLine();
            }
        }
    }

    public void listModules() {
        for (Module module : this.modules) {
            System.out.print(module.getName() + " => ");
            for (RequireStatement requireStatement : module.getRequires()) {
                System.out.print(requireStatement.getModulePath() + ", ");
            }
            System.out.println();
        }
    }
}
