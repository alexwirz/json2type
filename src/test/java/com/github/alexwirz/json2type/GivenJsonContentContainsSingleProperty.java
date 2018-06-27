package com.github.alexwirz.json2type;

import com.squareup.javapoet.JavaFile;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonContentContainsSingleProperty {
    private final JsonContent jsonContent;

    public GivenJsonContentContainsSingleProperty() {
        this.jsonContent = new JsonContent("{\"answer\" : 42}");
    }

    @Test
    public void thenJavaFilesHasSizeOne() throws IOException {
        assertThat(this.jsonContent.generateJavaFiles("Test")).hasSize(1);
    }

    @Test
    public void thenClassContainsSingleField() throws IOException {
        List<JavaFile> javaFiles = jsonContent.generateJavaFiles("Test");
        assertThat(javaFiles.get(0).toString()).contains("private final int answer;");
    }
}
