package org.gravitytwog;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        String root = "./examples/";
        String result = "./result.txt";

        Bundler bundler = new Bundler();
        bundler.loadModules(root);
        bundler.listModules();
        bundler.bundle(result);

        System.out.println("The result is saved in file: " + result);
    }
}