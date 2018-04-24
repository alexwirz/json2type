package com.github.alexwirz.json2type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaType {
    public static List<JavaFile> fromJson(String packageName, String mainClassName, String json) throws IOException {
        Map<String, Object> fields = parseFields(json);
        List<JavaFile> javaClasses =
                fields.entrySet().stream()
                        .filter(kv -> isComplex(kv.getValue()))
                        .map(kv -> buildClass(packageName, typeName(kv.getKey()), (Map<String, Object>) kv.getValue()))
                        .collect(Collectors.toList());
        javaClasses.add(buildClass(packageName, mainClassName, fields));
        return javaClasses;
    }

    private static String typeName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static boolean isComplex(Object object) {
        return object instanceof LinkedHashMap;
    }

    private static Map<String, Object> parseFields(String json) throws IOException {
        TypeReference<Map<String,Object>> mapTypeReference
                = new TypeReference<Map<String,Object>>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, mapTypeReference);
    }

    private static JavaFile buildClass(String packageName,
                                       String className,
                                       Map<String, Object> hashMap) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        fields(hashMap, packageName).forEach(classBuilder::addField);

        MethodSpec.Builder publicCtorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        publicCtorBuilder.addParameters(parameters(hashMap, packageName));
        publicCtorBuilder.addAnnotation(JsonCreator.class);
        hashMap.keySet().forEach(f -> publicCtorBuilder.addStatement("this.$N = $N", f, f));
        MethodSpec jsonCreatorCtorSpec = publicCtorBuilder.build();

        TypeSpec type = classBuilder
                        .addMethod(jsonCreatorCtorSpec)
                        .addMethods(geters(hashMap, packageName))
				        .build();
        return JavaFile.builder(packageName, type).build();
    }

    private static Iterable<MethodSpec> geters(Map<String, Object> fieldsMap, String packageName) {
        return fieldsMap.entrySet()
                .stream()
                .map(field -> buildGeter(field, packageName))
                .collect(Collectors.toList());
    }

    private static MethodSpec buildGeter(Map.Entry<String, Object> field, String packageName) {
        return MethodSpec.methodBuilder(formatGeterName(field.getKey()))
                .addStatement("return this.$N", field.getKey())
                .addModifiers(Modifier.PUBLIC)
                .returns(getTypeName(field, packageName))
                .build();
    }

    private static String formatGeterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static Iterable<ParameterSpec> parameters(Map<String, Object> fieldsMap, String packageName) {
	    return fieldsMap.entrySet()
                .stream()
                .map(entry -> buildParameter(entry, packageName))
                .collect(Collectors.toList());
    }

    private static ParameterSpec buildParameter(Map.Entry<String, Object> entry, String packageName) {
	    return ParameterSpec.builder(getTypeName(entry, packageName),
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

    private static Stream<FieldSpec> fields(Map<String, Object> fieldsMap, String packageName) {
	    return fieldsMap.entrySet().stream().map(entry -> buildField(entry, packageName));
    }

    private static FieldSpec buildField(Map.Entry<String, Object> entry, String packageName) {
        return FieldSpec
                .builder(
                    getTypeName(entry, packageName),
                    entry.getKey())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    public static TypeName getTypeName(Map.Entry<String, Object> entry, String packageName) {
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
