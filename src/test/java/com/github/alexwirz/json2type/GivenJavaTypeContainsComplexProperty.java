package com.github.alexwirz.json2type;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJavaTypeContainsComplexProperty {
    private final JavaType content;

    public GivenJavaTypeContainsComplexProperty() {
        this.content = new JavaType("{\"foo\" : {\"answer\" : 42}}");
    }

    @Test
    public void thenTwoJavaFilesGenerated() throws IOException {
        assertThat(this.content.generateJavaFiles("mypackage", "Test")).hasSize(2);
    }
}
