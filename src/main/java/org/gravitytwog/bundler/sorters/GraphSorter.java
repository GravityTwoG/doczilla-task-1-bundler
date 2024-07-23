package org.gravitytwog.bundler.sorters;

import org.gravitytwog.bundler.CycleFoundException;
import org.gravitytwog.parser.Module;

import java.util.List;

public interface GraphSorter {
    List<Module> sort(List<Module> modules) throws CycleFoundException;
}
