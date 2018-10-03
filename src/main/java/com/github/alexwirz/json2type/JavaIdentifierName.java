package com.github.alexwirz.json2type;

public class JavaIdentifierName {
    private String name;

    public JavaIdentifierName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if(!Character.isJavaIdentifierStart(name.toCharArray()[0])) {
            return "_" + replaceNonJavaIdentifierPartsWithUnderscore(name);
        }

        return replaceNonJavaIdentifierPartsWithUnderscore(name);
    }

    private String replaceNonJavaIdentifierPartsWithUnderscore(String name) {
        char[] chars = name.toCharArray();
        for(int i = 1; i < chars.length; ++i) {
            if(!Character.isJavaIdentifierStart(chars[i])) {
                chars[i] = '_';
            }
        }

        return new String(chars);
    }
}
