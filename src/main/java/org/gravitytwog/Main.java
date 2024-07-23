package org.gravitytwog;

import org.gravitytwog.bundler.Bundler;
import org.gravitytwog.bundler.sorters.GraphSorter;
import org.gravitytwog.bundler.sorters.KahnGraphSorter;
import org.gravitytwog.bundler.sorters.TarjanGraphSorter;

public class Main {
    public static void main(String[] args) throws Exception {
        String rootPath = "./examples/";
        String result = "./result.txt";

        GraphSorter kahnSorter = new KahnGraphSorter();
        GraphSorter tarjanSorter = new TarjanGraphSorter();

        Bundler bundler = new Bundler(rootPath, kahnSorter);
        bundler.loadModules();
        bundler.listModules();
        bundler.bundle(result);

        System.out.println("The result is saved in file: " + result);
    }
}