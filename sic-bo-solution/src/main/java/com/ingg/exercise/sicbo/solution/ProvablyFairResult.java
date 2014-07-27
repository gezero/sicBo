package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.Selection;

/**
 * @author Jiri
 */
public interface ProvablyFairResult {
    int calculatePrice(Selection selection, int stake);
    Iterable<Integer> getRoll();
    String getSalt();
}
