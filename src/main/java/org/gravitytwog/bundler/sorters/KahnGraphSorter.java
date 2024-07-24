package org.gravitytwog.bundler.sorters;

import org.gravitytwog.bundler.CycleFoundException;
import org.gravitytwog.parser.Module;

import java.util.*;

public class KahnGraphSorter implements GraphSorter {
    private final Map<Module, Set<Module>> outbound;
    private final Map<Module, Set<Module>> inbound;

    public KahnGraphSorter() {
        this.outbound = new HashMap<>();
        this.inbound = new HashMap<>();
    }

    // Kahn's Algorithm
    @Override
    public List<Module> sort(Map<Module, Set<Module>> graph) throws CycleFoundException {
        this.buildInboundDependenciesGraph(graph);

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
                List<Module> nodesInCycle = new ArrayList<>(this.inbound.keySet());
                throw new CycleFoundException(nodesInCycle);
            }
        }

        return result.reversed();
    }

    private void buildInboundDependenciesGraph(Map<Module, Set<Module>> graph) {
        this.outbound.clear();
        this.outbound.putAll(graph);
        this.inbound.clear();

        for (Module module : graph.keySet()) {
            inbound.put(module, new HashSet<>());
        }

        for (Module dependent : graph.keySet()) {
            Set<Module> dependencies = outbound.get(dependent);

            // add module to inbound dependents
            for (Module dependency : dependencies) {
                var dependents = inbound.get(dependency);
                dependents.add(dependent);
            }
        }
    }
}
