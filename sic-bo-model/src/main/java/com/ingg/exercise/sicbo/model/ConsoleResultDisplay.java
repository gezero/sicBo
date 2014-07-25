package com.ingg.exercise.sicbo.model;

import java.util.Iterator;

/**
 * <p>
 * A simple {@link ResultDisplay} which writes to <code>stdout</code>.
 * </p>
 *
 * @author iKernel Team
 * @author Inspired Gaming Group
 */
public class ConsoleResultDisplay implements ResultDisplay {

    @Override
    public void displayResult(String roundId, Iterable<Integer> result) {
        System.out.println(format(result));
    }

    private static String format(Iterable<Integer> result) {
        StringBuilder announcement = new StringBuilder("Result:\t[ ");
        for (Iterator<Integer> iterator = result.iterator(); iterator.hasNext(); ) {
            Integer die = iterator.next();
            announcement.append(die.toString());
            if (iterator.hasNext())
                announcement.append(", ");
        }
        return announcement.append(" ]").toString();
    }

}
