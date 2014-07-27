package com.ingg.exercise.sicbo.solution;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class SessionRandomGeneratorTest {

    SessionRandomGenerator randomGenerator = new SessionRandomGenerator();

    @Test
    public void testSomeGeneration() throws Exception {
        for (int i = 0; i < 1000; i++) {
            int i1 = randomGenerator.generateInteger(6);
            assertThat(i1, is(lessThan(6)));
            String s = randomGenerator.generateString();
            assertThat(s,is(notNullValue()));
            assertThat(s.length(),is(greaterThan(0)));
        }
    }

}