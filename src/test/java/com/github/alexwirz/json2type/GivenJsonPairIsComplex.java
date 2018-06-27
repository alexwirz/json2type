package com.github.alexwirz.json2type;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonPairIsComplex {
    private final JsonPair<LinkedHashMap> jsonPair;

    public GivenJsonPairIsComplex() {
        ImmutableMap<String, Object> map = ImmutableMap.<String, Object>builder().put("foo", "bar").put("answer", 42).build();
        this.jsonPair = new JsonPair<>("test", "inner", new LinkedHashMap(map));
    }

    @Test
    public void thenIsComplexIsTrue() {
        assertThat(jsonPair.isComplex()).isTrue();
    }

    @Test
    public void thenCtorParameterIsInner() {
        ParameterSpec ctorParmeter = jsonPair.generateCtorParameter();
        assertThat(ctorParmeter).isNotNull();
        assertThat(ctorParmeter.type.toString()).isEqualToIgnoringCase("test.Inner");
    }
}
