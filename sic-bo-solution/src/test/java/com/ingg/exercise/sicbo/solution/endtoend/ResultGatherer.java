package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.solution.ImmutableProvablyFairResult;
import com.ingg.exercise.sicbo.solution.ProvablyFairResult;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Jiri
 */
public class ResultGatherer implements ResultDisplay {
    Map<String, ProvablyFairResult> map = new HashMap<>();

    @Override
    public void displayResult(String roundId, Iterable<Integer> roll) {
        checkNotNull(roundId);
        checkNotNull(roll);
        if (map.get(roundId) != null) {
            throw new RuntimeException("There was already something in map!");
        }
        ImmutableProvablyFairResult result = new ImmutableProvablyFairResult(roll, null);
        System.out.println("The result of round " + roundId + " is "+result);
        map.put(roundId,result);
    }

    public boolean check(Selection selection, int stake, BetFuture betFuture) throws InterruptedException {
        //This is unfortunately to complicated because Triple constant is not in the Selection Enum
        ProvablyFairResult provablyFairResult = map.get(betFuture.getRoundId());
        assertThat(provablyFairResult, is(notNullValue()));
        Integer expectedPrice = provablyFairResult.calculatePrice(selection,stake);
        boolean check = expectedPrice.equals(betFuture.getPrize());
        if (!check) {
            System.out.println("Bet for round " + betFuture.getRoundId() + "should have been different...");
            System.out.println("Our selection: " + selection + " Round was: " + provablyFairResult);
            System.out.println("We got: " + betFuture.getPrize() + " Should get: " + expectedPrice);
        }

        return check;
    }
}