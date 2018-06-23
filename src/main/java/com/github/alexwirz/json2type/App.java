package com.github.alexwirz.json2type;

import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        OptionParser parser = new OptionParser();// "p:c:s:" );
        OptionSpec<String> packageSpec = parser.accepts("package", "Package name").withRequiredArg().ofType(String.class);
        OptionSpec<String> classSpec = parser.accepts("class", "Main class name").withRequiredArg().ofType(String.class);
        OptionSpec<String> sourceSpec = parser.accepts("source", "Source json file").withRequiredArg().ofType(String.class);
        OptionSet optionsSet = parser.parse(args);
        if(!optionsSet.has("f") || !optionsSet.has("c") || !optionsSet.has("s")) {
            System.err.println("Arguments missing:");
            parser.printHelpOn(System.err);
            System.exit(1);
        }

        JavaPackage.fromJson(
            optionsSet.valueOf(packageSpec),
            optionsSet.valueOf(classSpec),
            optionsSet.valueOf(sourceSpec));
    }
}
