package org.gravitytwog.bundler;

import org.gravitytwog.parser.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleFoundException extends Exception {
    List<Module> modules;

    public CycleFoundException(List<Module> nodesInCycles) {
        super("Cycle found");
        this.modules = nodesInCycles;
    }

    @Override
    public String toString() {
        Map<String, Module> map = new HashMap<>();

        for (Module module : this.modules) {
            map.put(module.getName(), module);
        }

        StringBuilder sb = new StringBuilder("Cycle found:\n");

        for (var module : this.modules) {
            sb.append("Module [").append(module.getName()).append("] ");
            for (var depName : module.getDependencies()) {
                if (map.containsKey(depName)) {
                    sb.append("depends on [").append(depName).append("]\n");
                }
            }
        }

        return sb.toString();
    }
}
