package com.github.alexwirz.json2type;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonContentIsEmpty {
    private final JsonContent json;

    public GivenJsonContentIsEmpty() {
        this.json = new JsonContent("{}");
    }

    @Test
    public void thenNoFields() throws IOException {
        assertThat(this.json.fields()).hasSize(0);
    }

    @Test
    public void thenJavaFilesHasSizeOne() throws IOException {
        assertThat(this.json.generateJavaFiles("Test")).hasSize(1);
    }
}
