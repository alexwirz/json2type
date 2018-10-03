package com.github.alexwirz.json2type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class JavaProperty<TValue extends Object> {
    private final String packageName;
    private final String key;
    private final TValue value;

    public JavaProperty(String packageName, final String key, final TValue value) {
        this.packageName = packageName;
        this.key = key;
        this.value = value;
    }


    public boolean isComplex() {
        return value instanceof LinkedHashMap;
    }

    public ParameterSpec generateCtorParameter() {
        return ParameterSpec.builder(fullyQualifiedTypeName(), key)
                .addModifiers(Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(JsonProperty.class)
                        .addMember("value", "$S", key)
                        .build())
                .build();
    }

    public FieldSpec generateField() {
        return FieldSpec
                .builder(fullyQualifiedTypeName(), key)
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PRIVATE)
                .build();
    }

    public MethodSpec generateGetter() {
        return MethodSpec.methodBuilder(formatGeterName())
                .returns(fullyQualifiedTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$N", key)
                .build();
    }

    private String formatGeterName() {
        return "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    public CodeBlock generateAssignment() {
        return CodeBlock.of("this.$N = $N", key, key);
    }

    public List<JavaProperty<?>> fields() {
        return isComplex() ?
                ((Map<String, Object>)value).entrySet().stream().map(x -> new JavaProperty<>(packageName, x.getKey(), x.getValue())).collect(Collectors.toList())
                : Collections.emptyList();
    }

    private TypeName fullyQualifiedTypeName() {
        if (isComplex()) {
            return ClassName.get(packageName, shortTypeName());
        }

        return getTypeNameForClass();
    }

    private TypeName getTypeNameForClass() {
        TypeName typeName = TypeName.get(value.getClass());
        return maybeArrayTypeName(typeName).orElseGet(() -> maybeUnbox(typeName));
    }

    private Optional<TypeName> maybeArrayTypeName(TypeName typeName) {
        TypeName listTypeName = TypeName.get(ArrayList.class);
        if(typeName.equals(listTypeName)) {
            return Optional.of(arrayTypeName());
        }

        return Optional.empty();
    }

    private TypeName arrayTypeName() {
        ArrayList arrayList = (ArrayList) this.value;
        return ArrayTypeName.of(maybeUnbox(TypeName.get(arrayList.get(0).getClass())));
    }

    private TypeName maybeUnbox(TypeName typeName) {
        return typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
    }

    public String shortTypeName() {
        return key.substring(0, 1).toUpperCase() + key.substring(1);
    }
}
