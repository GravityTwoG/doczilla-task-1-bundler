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

    private final List<List<Module>> strongConnectionComponents;

    public TarjanGraphSorter() {
        this.graph = new HashMap<>();
        this.index = 0;
        this.stack = new Stack<>();
        this.onStack = new HashSet<>();
        this.indexes = new HashMap<>();
        this.lowLinks = new HashMap<>();
        this.strongConnectionComponents = new ArrayList<>();
    }

    @Override
    public List<Module> sort(Map<Module, Set<Module>> graph) throws CycleFoundException {
        this.reset();
        this.graph.putAll(graph);

        this.findSCCs();

        List<Module> nodesInCycles = new ArrayList<>();
        for (List<Module> strongConnection : this.strongConnectionComponents) {
            if (strongConnection.size() > 1) {
                nodesInCycles.addAll(strongConnection);
                throw  new CycleFoundException(nodesInCycles);
            }
        }

        List<Module> sorted = new ArrayList<>();
        for (List<Module> scc : this.strongConnectionComponents) {
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
        this.strongConnectionComponents.clear();
    }

    public void findSCCs() {
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
                    this.findStrongConnection(w);
                    this.lowLinks.put(v, Math.min(this.lowLinks.get(v), this.lowLinks.get(w)));
                } else if (onStack.contains(w)) {
                    this.lowLinks.put(v, Math.min(this.lowLinks.get(v), this.indexes.get(w)));
                }
            }
        }

        if (this.lowLinks.get(v).equals(this.indexes.get(v))) {
            List<Module> strongConnectionComponent = new ArrayList<>();
            Module w;
            do {
                w = this.stack.pop();
                this.onStack.remove(w);
                strongConnectionComponent.add(w);
            } while (w != v);
            this.strongConnectionComponents.add(strongConnectionComponent);
        }
    }
}