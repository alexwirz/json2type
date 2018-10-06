package com.github.alexwirz.json2type;

import com.squareup.javapoet.FieldSpec;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenValueIsNull {
    private final JavaProperty<Object> javaProperty;

    public GivenValueIsNull() {
        this.javaProperty = new JavaProperty<>("test", "foo", null);
    }

    @Test
    public void thenFieldHasTypeObject() {
        FieldSpec fieldSpec = javaProperty.generateField();
        assertThat(fieldSpec.type.toString()).isEqualToIgnoringCase("java.lang.Object");
    }
}
