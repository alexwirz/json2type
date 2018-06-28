package com.github.alexwirz.json2type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaType {
    private String json;

    public JavaType(String json) {
        this.json = json;
    }

    public List<JavaProperty<?>> fields(String packageName) throws IOException {
        TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, mapTypeReference);
        return map.entrySet().stream()
                .map(x -> new JavaProperty<>(packageName, x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    public List<JavaFile> generateJavaFiles(String packageName, String className) throws IOException {
        return generateJavaFiles(packageName, className, fields(packageName));
    }

    public List<JavaFile> generateJavaFiles(String packageName, String className, List<JavaProperty<?>> fields) {
        List<JavaFile> javaFiles = innerTypes(packageName, fields);
        javaFiles.add(containingType(packageName, className, fields));
        return javaFiles;
    }

    private List<JavaFile> innerTypes(String packageName, List<JavaProperty<?>> fields) {
        return fields.stream()
                .filter(JavaProperty::isComplex)
                .map(kv -> generateJavaFiles(packageName, kv.shortTypeName(), kv.fields()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private JavaFile containingType(String packageName, String className, List<JavaProperty<?>> fields) {
        MethodSpec.Builder publicCtorBuilder =
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(JsonCreator.class)
                        .addParameters(fields.stream().map(JavaProperty::generateCtorParameter).collect(Collectors.toList()));
        fields.forEach(f -> publicCtorBuilder.addStatement(f.generateAssignment()));
        MethodSpec publicCtor = publicCtorBuilder.build();
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(publicCtor)
                .addMethods(fields.stream().map(JavaProperty::generateGetter).collect(Collectors.toList()));
        fields.forEach(f -> classBuilder.addField(f.generateField()));
        return JavaFile.builder(packageName, classBuilder.build()).build();
    }
}
