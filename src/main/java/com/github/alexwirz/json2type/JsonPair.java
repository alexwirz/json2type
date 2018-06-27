package com.github.alexwirz.json2type;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;

public class JsonPair<TValue extends Object> {
    private final String packageName;
    private final String key;
    private final TValue value;

    public JsonPair(String packageName, final String key, final TValue value) {
        this.packageName = packageName;
        this.key = key;
        this.value = value;
    }


    public boolean isComplex() {
        return value instanceof LinkedHashMap;
    }

    public ParameterSpec generateCtorParameter() {
        return ParameterSpec.builder(JavaPackage.getTypeName(packageName, key, value), key)
                .addModifiers(Modifier.FINAL)
                .addAnnotation(JavaPackage.jsonPropertyAnnotation(key))
                .build();
    }

    public FieldSpec generateField() {
        return FieldSpec
                .builder(JavaPackage.getTypeName(packageName, key, value), key)
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    public MethodSpec generateGetter() {
        return MethodSpec.methodBuilder(JavaPackage.formatGeterName(key))
                .returns(JavaPackage.getTypeName(packageName, key, value))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$N", key)
                .build();
    }
}
