package com.github.alexwirz.json2type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaType {

	public static String fromJson(String packageName, String className, String json) throws IOException {
        TypeReference<Map<String,Object>> mapTypeReference
                = new TypeReference<Map<String,Object>>() {};

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> hashMap = objectMapper.readValue(json, mapTypeReference);

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        fields(hashMap).forEach(classBuilder::addField);

        MethodSpec.Builder publicCtorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        publicCtorBuilder.addParameters(parameters(hashMap));
        publicCtorBuilder.addAnnotation(JsonCreator.class);
        hashMap.keySet().forEach(f -> publicCtorBuilder.addStatement("this.$N = $N", f, f));
        MethodSpec jsonCreatorCtorSpec = publicCtorBuilder.build();

        TypeSpec type = classBuilder
                        .addMethod(jsonCreatorCtorSpec)
                        .addMethods(geters(hashMap))
				        .build();
        JavaFile javaFile = JavaFile.builder(packageName, type).build();
		return javaFile.toString();
	}

    private static Iterable<MethodSpec> geters(Map<String, Object> fieldsMap) {
        return fieldsMap.entrySet()
                .stream()
                .map(JavaType::buildGeter)
                .collect(Collectors.toList());
    }

    private static MethodSpec buildGeter(Map.Entry<String, Object> field) {
        return MethodSpec.methodBuilder(formatGeterName(field.getKey()))
                .addStatement("return this.$N", field.getKey())
                .addModifiers(Modifier.PUBLIC)
                .returns(tryGetPrimitiveType(field.getValue().getClass()))
                .build();
    }

    private static String formatGeterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static Iterable<ParameterSpec> parameters(Map<String, Object> fieldsMap) {
	    return fieldsMap.entrySet()
                .stream()
                .map(JavaType::buildParameter)
                .collect(Collectors.toList());
    }

    private static ParameterSpec buildParameter(Map.Entry<String, Object> entry) {
	    return ParameterSpec.builder(tryGetPrimitiveType(entry.getValue().getClass()),
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

    private static Stream<FieldSpec> fields(Map<String, Object> fieldsMap) {
	    return fieldsMap.entrySet().stream().map(JavaType::buildField);
    }

    private static FieldSpec buildField(Map.Entry<String, Object> enty) {
        return FieldSpec.builder(
                tryGetPrimitiveType(enty.getValue().getClass()),
                enty.getKey())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build();
    }

    public static TypeName tryGetPrimitiveType(Type name) {
        TypeName typeName = TypeName.get(name);
        return typeName.isBoxedPrimitive() ? typeName.unbox() : typeName;
    }
}
