package com.github.alexwirz.json2type;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JavaIdentifierNameTest {
    @Test
    public void foo_is_foo() {
        assertThat(new JavaIdentifierName("foo").toString(), is("foo"));
    }

    @Test
    public void bar_is_bar() {
        assertThat(new JavaIdentifierName("bar").toString(), is("bar"));
    }

    @Test
    public void minusReplacedWithUnderscore() {
        assertThat(new JavaIdentifierName("foo-bar").toString(), is("foo_bar"));
    }

    @Test
    public void givenNameStartsWithNumber_sanitizedNameStartsWithUnderscore() {
        assertThat(new JavaIdentifierName("1test").toString(), is("_1test"));
    }

    @Test
    public void nonAlphaNumberReplacedWithUnderscore() {
        assertThat(new JavaIdentifierName("a!%&*#?").toString(), is("a______"));
    }
}
