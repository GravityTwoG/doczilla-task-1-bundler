package org.gravitytwog.bundler.sorters;

import org.gravitytwog.bundler.CycleFoundException;
import org.gravitytwog.parser.Module;

import java.util.*;

public class TarjanGraphSorter implements GraphSorter {

    private final Map<Module, Set<Module>> graph;
    private int index;

    private final Stack<Module> stack;
    private final Set<Module> onStack;

    private final Map<Module, Integer> indexes;
    private final Map<Module, Integer> lowLinks;

    private final List<List<Module>> strongConnections;

    public TarjanGraphSorter() {
        this.graph = new HashMap<>();
        this.index = 0;
        this.stack = new Stack<>();
        this.onStack = new HashSet<>();
        this.indexes = new HashMap<>();
        this.lowLinks = new HashMap<>();
        this.strongConnections = new ArrayList<>();
    }

    @Override
    public List<Module> sort(List<Module> modules) throws CycleFoundException {
        this.reset();

        this.findSCCs(modules);

        List<Module> nodesInCycles = new ArrayList<>();
        for (List<Module> strongConnection : this.strongConnections) {
            if (strongConnection.size() > 1) {
                nodesInCycles.addAll(strongConnection);
                throw  new CycleFoundException(nodesInCycles);
            }
        }

        List<Module> sorted = new ArrayList<>();
        for (List<Module> scc : this.strongConnections) {
            sorted.addAll(scc);
        }

        return sorted;
    }

    private void reset() {
        this.graph.clear();
        this.index = 0;
        this.stack.clear();
        this.onStack.clear();
        this.indexes.clear();
        this.lowLinks.clear();
        this.strongConnections.clear();
    }

    public void findSCCs(List<Module> modules) {
        this.buildDependencyGraph(modules);

        for (var v : this.graph.keySet()) {
            if (!this.indexes.containsKey(v)) {
                this.findStrongConnection(v);
            }
        }
    }

    private void findStrongConnection(Module v) {
        this.indexes.put(v, this.index);
        this.lowLinks.put(v, this.index);
        this.index++;
        this.stack.push(v);
        this.onStack.add(v);

        if (this.graph.containsKey(v)) {
            for (var w : this.graph.get(v)) {
                if (!this.indexes.containsKey(w)) {
                    findStrongConnection(w);
                    this.lowLinks.put(v, Math.min(this.lowLinks.get(v), this.lowLinks.get(w)));
                } else if (onStack.contains(w)) {
                    this.lowLinks.put(v, Math.min(this.lowLinks.get(v), this.indexes.get(w)));
                }
            }
        }

        if (this.lowLinks.get(v).equals(this.indexes.get(v))) {
            List<Module> scc = new ArrayList<>();
            Module w;
            do {
                w = this.stack.pop();
                this.onStack.remove(w);
                scc.add(w);
            } while (w != v);
            this.strongConnections.add(scc);
        }
    }

    private void buildDependencyGraph(List<Module> modules) {
        Map<String, Module> map = new HashMap<>();

        for (Module module : modules) {
            map.put(module.getName(), module);
            this.graph.put(module, new HashSet<>());
        }

        for (Module dependent : modules) {
            Set<Module> dependencies = this.graph.get(dependent);

            for (String dependencyName : dependent.getDependencies()) {
                Module dependency = map.get(dependencyName);
                dependencies.add(dependency);
            }
        }
    }
}