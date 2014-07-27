package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.Selection;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ImmutableRoundResultTest {
    ImmutableRoundResult small = new ImmutableRoundResult(Arrays.asList(1, 2, 3));
    ImmutableRoundResult small_tripple = new ImmutableRoundResult(Arrays.asList(2, 2, 2));
    ImmutableRoundResult big = new ImmutableRoundResult(Arrays.asList(4, 5, 5));
    ImmutableRoundResult big_triple = new ImmutableRoundResult(Arrays.asList(6, 6, 6));

    @Test
    public void testCalculatePrice() throws Exception {
        assertThat(small.calculatePrice(Selection.SMALL, 10), is(20));
        assertThat(small.calculatePrice(Selection.BIG, 10), is(0));

        assertThat(big.calculatePrice(Selection.SMALL, 10), is(0));
        assertThat(big.calculatePrice(Selection.BIG, 10), is(20));

        assertThat(small_tripple.calculatePrice(Selection.SMALL, 10), is(0));
        assertThat(small_tripple.calculatePrice(Selection.BIG, 10), is(0));

        assertThat(big_triple.calculatePrice(Selection.SMALL, 10), is(0));
        assertThat(big_triple.calculatePrice(Selection.BIG, 10), is(0));

    }

    @Test
    public void testToString() throws Exception {
        assertThat(small.toString(), is("SMALL"));
        assertThat(small_tripple.toString(), is("TRIPLE"));
        assertThat(big.toString(), is("BIG"));
        assertThat(big_triple.toString(), is("TRIPLE"));
    }

    @Test
    public void testAllCombinations() throws Exception {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {

                    ImmutableRoundResult result = null;
                    try {
                        result = new ImmutableRoundResult(Arrays.asList(i, j, k));
                        assertThat(i >= 1 && i <= 6 && j >= 1 && j <= 6 && k >= 1 && k <= 6, is(true));
                        if (i == j && i == k) {
                            assertThat(result.toString(), is("TRIPLE"));
                        }else {
                            if (i + j + k > 10) {
                                assertThat(result.toString(), is("BIG"));
                            } else {
                                assertThat(result.toString(), is("SMALL"));
                            }
                        }
                    } catch (ArithmeticException e) {
                        assertThat(i >= 1 && i <= 6 && j >= 1 && j <= 6 && k >= 1 && k <= 6, is(false));
                    }

                }
            }
        }
    }
}