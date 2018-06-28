package com.github.alexwirz.json2type;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJavaPropertyIsComplex {
    private final JavaProperty<LinkedHashMap> javaProperty;

    public GivenJavaPropertyIsComplex() {
        ImmutableMap<String, Object> map = ImmutableMap.<String, Object>builder().put("foo", "bar").put("answer", 42).build();
        this.javaProperty = new JavaProperty<>("test", "inner", new LinkedHashMap(map));
    }

    @Test
    public void thenIsComplexIsTrue() {
        assertThat(javaProperty.isComplex()).isTrue();
    }

    @Test
    public void thenCtorParameterIsInner() {
        ParameterSpec ctorParmeter = javaProperty.generateCtorParameter();
        assertThat(ctorParmeter).isNotNull();
        assertThat(ctorParmeter.type.toString()).isEqualToIgnoringCase("test.Inner");
    }

}
