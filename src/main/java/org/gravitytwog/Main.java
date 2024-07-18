package org.gravitytwog;

public class Main {
    public static void main(String[] args) throws Exception {
        String rootPath = "./examples/";
        String result = "./result.txt";

        Bundler bundler = new Bundler(rootPath);
        bundler.loadModules();
        bundler.listModules();
        bundler.bundle(result);

        System.out.println("The result is saved in file: " + result);
    }
}