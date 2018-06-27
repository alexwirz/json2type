package com.github.alexwirz.json2type;

import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonPropertyIsIntegerTest {
    private final JsonPair<Integer> jsonPair;

    public GivenJsonPropertyIsIntegerTest() {
        this.jsonPair = new JsonPair<>("test", "answer", 42);
    }

    @Test
    public void thenIsComplexIsFalse() {
        assertThat(jsonPair.isComplex()).isFalse();
    }

    @Test
    public void thenCtorParameterIsInteger() {
        ParameterSpec ctorParmeter = jsonPair.generateCtorParameter();
        assertThat(ctorParmeter).isNotNull();
        assertThat(ctorParmeter.type.toString()).isEqualToIgnoringCase("int");
    }

    @Test
    public void thenCtorParameterIsFinal() {
        ParameterSpec ctorParmeter = jsonPair.generateCtorParameter();
        assertThat(ctorParmeter.modifiers).contains(Modifier.FINAL);
    }

    @Test
    public void thenCtorParameterHasJsonPropertyAnnotation() {
        ParameterSpec ctorParmeter = jsonPair.generateCtorParameter();
        assertThat(ctorParmeter.annotations.get(0).toString())
                .isEqualToIgnoringCase("@com.fasterxml.jackson.annotation.JsonProperty(\"answer\")");
    }
}
