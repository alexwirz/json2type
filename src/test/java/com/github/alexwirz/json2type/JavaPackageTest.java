package com.github.alexwirz.json2type;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaPackageTest {
	@Test
	public void createsEmptyClassForEmptyJsonObject() throws IOException {
		final String json = "{}";
		final String java =
                JavaPackage.fromJson("foo.bar", "Test", json).get(0).toString();
		final String expectedJava =
				"package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "\n" +
                        "public final class Test {\n" +
                        "  @JsonCreator\n" +
                        "  public Test() {\n" +
                        "  }\n" +
                        "}";
		assertThat(java).isEqualToIgnoringWhitespace(expectedJava);
	}

	@Test
	public void createsClassWithSingleIntForJsonObjectWithInt() throws IOException {
		final String json = "{\"baz\" : 1}";
		final String java =
                JavaPackage.fromJson("foo.bar", "Test", json).get(0).toString();
		final String expectedJava =
				"package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                        "\n" +
                        "public final class Test {\n" +
                        "  private final int baz;\n" +
                        "\n" +
                        "  @JsonCreator\n" +
                        "  public Test(@JsonProperty(\"baz\") final int baz) {\n" +
                        "    this.baz = baz;\n" +
                        "  }\n" +
                        "\n" +
                        "  public int getBaz() {\n" +
                        "    return this.baz;\n" +
                        "  }\n" +
                        "}";
		assertThat(java).isEqualToIgnoringWhitespace(expectedJava);
	}

	@Test
	public void createsClassWithIntAndStringForJsonObjectWithIntAndString() throws IOException {
		final String json = "{\"foo\" : 1, \"bar\" : \"baz\"}";
		final String java =
                JavaPackage.fromJson("foo.bar", "Test", json).get(0).toString();
		final String expectedJava =
				"package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                        "import java.lang.String;\n" +
                        "\n" +
                        "public final class Test {\n" +
                        "  private final int foo;\n" +
                        "\n" +
                        "  private final String bar;\n" +
                        "\n" +
                        "  @JsonCreator\n" +
                        "  public Test(@JsonProperty(\"foo\") final int foo, @JsonProperty(\"bar\") final String bar) {\n" +
                        "    this.foo = foo;\n" +
                        "    this.bar = bar;\n" +
                        "  }\n" +
                        "\n" +
                        "  public int getFoo() {\n" +
                        "    return this.foo;\n" +
                        "  }\n" +
                        "\n" +
                        "  public String getBar() {\n" +
                        "    return this.bar;\n" +
                        "  }\n" +
                        "}\n";
		assertThat(java).isEqualTo(expectedJava);
	}

	@Test
	public void createsGeter() throws IOException {
		final String json = "{\"baz\" : 1}";
		final String java =
				JavaPackage.fromJson("foo.bar", "Test", json).get(0).toString();
		final String expectedJava =
				"package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                        "\n" +
                        "public final class Test {\n" +
                        "  private final int baz;\n" +
                        "\n" +
                        "  @JsonCreator\n" +
                        "  public Test(@JsonProperty(\"baz\") final int baz) {\n" +
                        "    this.baz = baz;\n" +
                        "  }\n" +
                        "\n" +
                        "  public int getBaz() {\n" +
                        "    return this.baz;\n" +
                        "  }\n" +
                        "}";
		assertThat(java).isEqualToIgnoringWhitespace(expectedJava);
	}

	@Test
	public void createsSeparateClassesForNestedTypes() throws IOException {
	    final String json = "{\"foo\" : {\"bar\" : 42}}";
        final List<JavaFile> src = JavaPackage.fromJson("foo.bar", "Test", json);
        assertThat(src).hasSize(2);
        final String expectedTestClass =
                "package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                        "\n" +
                        "public final class Test {\n" +
                        "  private final Foo foo;\n" +
                        "\n" +
                        "  @JsonCreator\n" +
                        "  public Test(@JsonProperty(\"foo\") final Foo foo) {\n" +
                        "    this.foo = foo;\n" +
                        "  }\n" +
                        "\n" +
                        "  public Foo getFoo() {\n" +
                        "    return this.foo;\n" +
                        "  }\n" +
                        "}";
        assertThat(src.get(1).toString()).isEqualToIgnoringWhitespace(expectedTestClass);
        final String expectedFooClass =
                "package foo.bar;\n" +
                        "\n" +
                        "import com.fasterxml.jackson.annotation.JsonCreator;\n" +
                        "import com.fasterxml.jackson.annotation.JsonProperty;\n" +
                        "\n" +
                        "public final class Foo {\n" +
                        "  private final int bar;\n" +
                        "\n" +
                        "  @JsonCreator\n" +
                        "  public Foo(@JsonProperty(\"bar\") final int bar) {\n" +
                        "    this.bar = bar;\n" +
                        "  }\n" +
                        "\n" +
                        "  public int getBar() {\n" +
                        "    return this.bar;\n" +
                        "  }\n" +
                        "}";
        assertThat(src.get(0).toString()).isEqualToIgnoringWhitespace(expectedFooClass);
    }

	@Test
	public void createsThreeSeparateClassesForThreeNestedTypes() throws IOException {
        final String json = "{\"foo\" : {\"bar\" : {\"answer\" : 42}}}";
        final List<JavaFile> src = JavaPackage.fromJson("foo.bar", "Test", json);
        assertThat(src).hasSize(3);
    }

    @Test
    public void createsIntArrayForIntArrays() throws IOException {
		final String json = "{\"array\" : [1, 2]}";
		final String expectedCode = "package foo;\n" +
				"\n" +
				"import com.fasterxml.jackson.annotation.JsonCreator;\n" +
				"import com.fasterxml.jackson.annotation.JsonProperty;\n" +
				"\n" +
				"public final class Test {\n" +
				"  private final int[] array;\n" +
				"\n" +
				"  @JsonCreator\n" +
				"  public Test(@JsonProperty(\"array\") final int[] array) {\n" +
				"    this.array = array;\n" +
				"  }\n" +
				"\n" +
				"  public int[] getArray() {\n" +
				"    return this.array;\n" +
				"  }\n" +
				"}\n";
		final List<JavaFile> src = JavaPackage.fromJson("foo", "Test", json);
		assertThat(src.get(0).toString()).isEqualToIgnoringWhitespace(expectedCode);
	}

	@Test
	public void createsStringArrayForStringArrays() throws IOException {
		final String json = "{\"array\" : [\"a\", \"b\"]}";
		final String expectedCode = "package foo;\n" +
				"\n" +
				"import com.fasterxml.jackson.annotation.JsonCreator;\n" +
				"import com.fasterxml.jackson.annotation.JsonProperty;\n" +
				"import java.lang.String;\n" +
				"\n" +
				"public final class Test {\n" +
				"  private final String[] array;\n" +
				"\n" +
				"  @JsonCreator\n" +
				"  public Test(@JsonProperty(\"array\") final String[] array) {\n" +
				"    this.array = array;\n" +
				"  }\n" +
				"\n" +
				"  public String[] getArray() {\n" +
				"    return this.array;\n" +
				"  }\n" +
				"}\n";
		final List<JavaFile> src = JavaPackage.fromJson("foo", "Test", json);
		assertThat(src.get(0).toString()).isEqualToIgnoringWhitespace(expectedCode);
	}
}
