package com.ingg.exercise.sicbo.solution;

import com.ingg.exercise.sicbo.model.Selection;

/**
 * @author Jiri
 */
public class ImmutableRoundResult implements RoundResult {
    private final Selection selection;
    private final boolean isTriple;

    public ImmutableRoundResult(Iterable<Integer> roll) {
        selection = calculateSelection(roll);
        isTriple = isTriple(roll);
    }

    public static boolean isTriple(Iterable<Integer> roll) {
        Integer number = null;
        for (Integer integer : roll) {
            if (number == null) {
                number = integer;
            }
            if (!number.equals(integer)) {
                return false;
            }
        }
        return true;
    }

    public static Selection calculateSelection(Iterable<Integer> roll) {
        int total = 0;
        for (Integer integer : roll) {
            if (integer<1 || integer>6){
                throw new ArithmeticException("roll was outside of interval 1..6, was "+ integer);
            }
            total += integer;
        }
        return total > 10 ? Selection.BIG : Selection.SMALL;
    }


    @Override
    public int calculatePrice(Selection selection, int stake) {
        if (isTriple) {
            return 0;
        }
        return this.selection.equals(selection) ? 2 * stake : 0;
    }

    @Override
    public String toString() {
        if (isTriple){
            return "TRIPLE";
        }
        return selection.toString();
    }
}
