package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.Selection;

/**
 * @author Jiri
 */
public interface RoundResult {
    int calculatePrice(Selection selection, int stake);
}
