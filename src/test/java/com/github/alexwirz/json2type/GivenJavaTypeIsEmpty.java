package com.github.alexwirz.json2type;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJavaTypeIsEmpty {
    private final JavaType json;

    public GivenJavaTypeIsEmpty() {
        this.json = new JavaType("{}");
    }

    @Test
    public void thenNoFields() throws IOException {
        assertThat(this.json.fields("test")).hasSize(0);
    }

    @Test
    public void thenSingleJavaFileGenerated() throws IOException {
        assertThat(this.json.generateJavaFiles("mypackage", "Test")).hasSize(1);
    }


    @Test
    public void thenJavaFileContainsPackage() throws IOException {
        assertThat(json.generateJavaFiles("mypackage", "Test").get(0).packageName).isEqualToIgnoringCase("mypackage");
    }
}
