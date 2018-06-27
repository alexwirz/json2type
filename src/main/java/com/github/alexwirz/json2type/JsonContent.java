package com.github.alexwirz.json2type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonContent {
    private String json;

    public JsonContent(String json) {
        this.json = json;
    }

    public List<JsonPair<?>> fields() throws IOException {
        TypeReference<Map<String,Object>> mapTypeReference = new TypeReference<>() {};
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, mapTypeReference);
        return map.entrySet().stream()
                .map(x -> new JsonPair<>("test", x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    public List<JavaFile> generateJavaFiles(String className) throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        fields().forEach(f -> classBuilder.addField(f.generateField()));
        TypeSpec typeSpec = classBuilder.build();
        return Collections.singletonList(JavaFile.builder("test", typeSpec).build());
    }
}
