package com.github.alexwirz.json2type;

import com.squareup.javapoet.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class JavaPackage {
    private final String packageName;
    private final String mainClassName;

    public JavaPackage(String packageName, String mainClassName) {
        this.packageName = packageName;
        this.mainClassName = mainClassName;
    }

    public static List<JavaFile> fromJsonFile(String packageName, String mainClassName, String jsonFileName) throws IOException {
        return fromJson(packageName, mainClassName, new String(Files.readAllBytes(Paths.get(jsonFileName)), "UTF-8"));
    }

    public static List<JavaFile> fromJson(String packageName, String mainClassName, String json) throws IOException {
        JavaPackage javaPackage = new JavaPackage(packageName, mainClassName);
        return javaPackage.generateCodeFrom(json);
    }

    private List<JavaFile> generateCodeFrom(String json) throws IOException {
        return new JavaType(json).generateJavaFiles(packageName, mainClassName);
    }
}
