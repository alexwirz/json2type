package com.github.alexwirz.json2type;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

public class GivenJsonPropertyIsStringTest {
    private final JsonPair<String> jsonPair;

    public GivenJsonPropertyIsStringTest() {
        this.jsonPair = new JsonPair<>("foo", "bar");
    }

    @Test
    public void thenCtorParameterIsString() {
        ParameterSpec ctorParmeter = jsonPair.generateCtorParameter();
        assertThat(ctorParmeter).isNotNull();
        assertThat(ctorParmeter.type.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenFieldHasTypeString() {
        FieldSpec fieldSpec = jsonPair.generateField();
        assertThat(fieldSpec.type.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenFieldIsFinal() {
        FieldSpec fieldSpec = jsonPair.generateField();
        assertThat(fieldSpec.modifiers).contains(Modifier.FINAL);
    }

    @Test
    public void thenFieldIsPrivate() {
        FieldSpec fieldSpec = jsonPair.generateField();
        assertThat(fieldSpec.modifiers).contains(Modifier.PRIVATE);
    }

    @Test
    public void thenGetterReturnsString() {
        MethodSpec methodSpec = jsonPair.generateGetter();
        assertThat(methodSpec.returnType.toString()).isEqualToIgnoringCase("java.lang.String");
    }

    @Test
    public void thenGetterIsPublic() {
        MethodSpec methodSpec = jsonPair.generateGetter();
        assertThat(methodSpec.modifiers).contains(Modifier.PUBLIC);
    }

    @Test
    public void thenGetterHasReturnStatement() {
        MethodSpec methodSpec = jsonPair.generateGetter();
        final String expectedCode = "public java.lang.String getFoo() {\n" +
                "  return this.foo;\n" +
                "}\n";
        assertThat(methodSpec.toString()).isEqualToIgnoringCase(expectedCode);
    }
}

