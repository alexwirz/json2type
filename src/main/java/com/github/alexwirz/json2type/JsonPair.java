package com.github.alexwirz.json2type;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;

public class JsonPair<TValue extends Object> {
    private final String key;
    private final TValue value;

    public JsonPair(final String key, final TValue value) {
        this.key = key;
        this.value = value;
    }


    public boolean isComplex() {
        return false;
    }

    public ParameterSpec generateCtorParameter() {
        return ParameterSpec.builder(TypeName.get(value.getClass()), key)
                .addModifiers(Modifier.FINAL)
                .addAnnotation(JavaPackage.jsonPropertyAnnotation(key))
                .build();
    }

    public FieldSpec generateField() {
        return FieldSpec
                .builder(TypeName.get(value.getClass()), key)
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    public MethodSpec generateGetter() {
        return MethodSpec.methodBuilder(JavaPackage.formatGeterName(key))
                .returns(TypeName.get(value.getClass()))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$N", key)
                .build();
    }
}
