package com.github.alexwirz.json2type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaPackage {
    private final String packageName;
    private final String mainClassName;

    public JavaPackage(String packageName, String mainClassName) {
        this.packageName = packageName;
        this.mainClassName = mainClassName;
    }

    public static List<JavaFile> fromJson(String packageName, String mainClassName, String json) throws IOException {
        JavaPackage javaPackage = new JavaPackage(packageName, mainClassName);
        return javaPackage.generateCodeFrom(json);
    }

    public static List<JavaFile> fromJsonFile(String packageName, String mainClassName, String jsonFileName) throws IOException {
        return fromJson(packageName, mainClassName, new String(Files.readAllBytes(Paths.get(jsonFileName))));
    }

    private List<JavaFile> generateCodeFrom(String json) throws IOException {
        return buildClass(mainClassName, parseFields(json));
    }

    private static Map<String, Object> parseFields(String json) throws IOException {
        TypeReference<Map<String,Object>> mapTypeReference
                = new TypeReference<Map<String,Object>>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, mapTypeReference);
    }

    private static String typeName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static boolean isComplex(Object object) {
        return object instanceof LinkedHashMap;
    }

    private List<JavaFile> buildClass(String className, Map<String, Object> fields) {
        List<JavaFile> javaFiles = innerTypes(fields);
        javaFiles.add(containingType(className, fields));
        return javaFiles;
    }

    private JavaFile containingType(String className, Map<String, Object> fields) {
        MethodSpec.Builder publicCtorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        publicCtorBuilder.addParameters(parameters(fields));
        publicCtorBuilder.addAnnotation(JsonCreator.class);
        fields.keySet().forEach(f -> publicCtorBuilder.addStatement("this.$N = $N", f, f));
        MethodSpec jsonCreatorCtorSpec = publicCtorBuilder.build();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        fields(fields).forEach(classBuilder::addField);
        TypeSpec type = classBuilder
                .addMethod(jsonCreatorCtorSpec)
                .addMethods(geters(fields))
                .build();
        return JavaFile.builder(packageName, type).build();
    }

    private List<JavaFile> innerTypes(Map<String, Object> fields) {
        return fields.entrySet().stream()
                .filter(kv -> isComplex(kv.getValue()))
                .map(kv -> buildClass(typeName(kv.getKey()), (Map<String, Object>) kv.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Iterable<MethodSpec> geters(Map<String, Object> fieldsMap) {
        return fieldsMap.entrySet()
                .stream()
                .map(this::buildGeter)
                .collect(Collectors.toList());
    }

    private MethodSpec buildGeter(Map.Entry<String, Object> field) {
        return MethodSpec.methodBuilder(formatGeterName(field.getKey()))
                .addStatement("return this.$N", field.getKey())
                .addModifiers(Modifier.PUBLIC)
                .returns(getTypeName(field))
                .build();
    }

    private static String formatGeterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private Iterable<ParameterSpec> parameters(Map<String, Object> fieldsMap) {
	    return fieldsMap.entrySet()
                .stream()
                .map(this::buildParameter)
                .collect(Collectors.toList());
    }

    private ParameterSpec buildParameter(Map.Entry<String, Object> entry) {
	    return ParameterSpec.builder(getTypeName(entry),
                entry.getKey())
                .addModifiers(Modifier.FINAL)
                .addAnnotation(jsonPropertyAnnotation(entry.getKey()))
                .build();
    }

    private static AnnotationSpec jsonPropertyAnnotation(String propertyName) {
	    return AnnotationSpec.builder(JsonProperty.class)
                .addMember("value", "$S", propertyName)
                .build();
    }

    private Stream<FieldSpec> fields(Map<String, Object> fieldsMap) {
	    return fieldsMap.entrySet().stream().map(this::buildField);
    }

    private FieldSpec buildField(Map.Entry<String, Object> entry) {
        return FieldSpec
                .builder(
                    getTypeName(entry),
                    entry.getKey())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    private TypeName getTypeName(Map.Entry<String, Object> entry) {
        if(isComplex(entry.getValue())) {
            return ClassName.get(packageName, typeName(entry.getKey()));
        }

        return getTypeNameForClass(entry.getValue().getClass());
    }

    public static TypeName getTypeNameForClass(Class clazz) {
        TypeName typeName = TypeName.get(clazz);
        return typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
    }
}
