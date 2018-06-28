package com.github.alexwirz.json2type;

import com.squareup.javapoet.JavaFile;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJavaTypeContainsSingleProperty {
    private final JavaType javaType;

    public GivenJavaTypeContainsSingleProperty() {
        this.javaType = new JavaType("{\"answer\" : 42}");
    }

    @Test
    public void thenJavaFilesHasSizeOne() throws IOException {
        assertThat(this.javaType.generateJavaFiles("mypackage", "Test")).hasSize(1);
    }

    @Test
    public void thenClassContainsSingleField() throws IOException {
        List<JavaFile> javaFiles = javaType.generateJavaFiles("mypackage", "Test");
        assertThat(javaFiles.get(0).toString()).contains("private final int answer;");
    }

    @Test
    public void thenClassContainsJsonCreatorWithSingleParameter() throws IOException {
        List<JavaFile> javaFiles = javaType.generateJavaFiles("mypackage", "Test");
        assertThat(javaFiles.get(0).toString()).contains("@JsonCreator\n  public Test(@JsonProperty(\"answer\") final int answer)");
        assertThat(javaFiles.get(0).toString()).contains("this.answer = answer;");
    }

    @Test
    public void thenClassContainsGeter() throws IOException {
        List<JavaFile> javaFiles = javaType.generateJavaFiles("mypackage", "Test");
        assertThat(javaFiles.get(0).toString()).contains("public int getAnswer() {\n    return this.answer;\n  }");
    }
}
