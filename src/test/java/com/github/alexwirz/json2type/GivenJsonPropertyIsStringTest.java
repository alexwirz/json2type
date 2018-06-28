package com.github.alexwirz.json2type;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonPropertyIsStringTest {
    private final JavaProperty<String> javaProperty;

    public GivenJsonPropertyIsStringTest() {
        this.javaProperty = new JavaProperty<>("test", "foo", "bar");
    }

    @Test
    public void thenCtorParameterIsString() {
        ParameterSpec ctorParmeter = javaProperty.generateCtorParameter();
        assertThat(ctorParmeter).isNotNull();
        assertThat(ctorParmeter.type.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenFieldHasTypeString() {
        FieldSpec fieldSpec = javaProperty.generateField();
        assertThat(fieldSpec.type.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenFieldIsFinal() {
        FieldSpec fieldSpec = javaProperty.generateField();
        assertThat(fieldSpec.modifiers).contains(Modifier.FINAL);
    }

    @Test
    public void thenFieldIsPrivate() {
        FieldSpec fieldSpec = javaProperty.generateField();
        assertThat(fieldSpec.modifiers).contains(Modifier.PRIVATE);
    }

    @Test
    public void thenGetterReturnsString() {
        MethodSpec methodSpec = javaProperty.generateGetter();
        assertThat(methodSpec.returnType.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenGetterIsPublic() {
        MethodSpec methodSpec = javaProperty.generateGetter();
        assertThat(methodSpec.modifiers).contains(Modifier.PUBLIC);
    }

    @Test
    public void thenGetterHasReturnStatement() {
        MethodSpec methodSpec = javaProperty.generateGetter();
        final String expectedCode = "public java.lang.String getFoo() {\n" +
                "  return this.foo;\n" +
                "}\n";
        assertThat(methodSpec.toString()).isEqualToIgnoringCase(expectedCode);
    }
}

