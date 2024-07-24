package org.gravitytwog.bundler.sorters;

import org.gravitytwog.bundler.CycleFoundException;
import org.gravitytwog.parser.Module;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GraphSorter {
    List<Module> sort(Map<Module, Set<Module>> graph) throws CycleFoundException;
}
