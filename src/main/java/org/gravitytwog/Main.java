package org.gravitytwog;

import org.gravitytwog.bundler.Bundler;
import org.gravitytwog.bundler.sorters.GraphSorter;
import org.gravitytwog.bundler.sorters.TarjanGraphSorter;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("bundler <folder-path> <output-filename>");
            return;
        }

        String rootPath = args[0];
        String result = args[1];

//        GraphSorter kahnSorter = new KahnGraphSorter();
        GraphSorter tarjanSorter = new TarjanGraphSorter();

        Bundler bundler = new Bundler(rootPath, tarjanSorter);
        bundler.loadModules();
        bundler.listModules();
        bundler.bundle(result);

        System.out.println("The result is saved in file: " + result);
    }
}