package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.ResultDisplay;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.solution.ImmutableRoundResult;
import com.ingg.exercise.sicbo.solution.RoundResult;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Jiri
 */
public class ResultGatherer implements ResultDisplay {
    Map<String, RoundResult> map = new HashMap<>();

    @Override
    public void displayResult(String roundId, Iterable<Integer> roll) {
        checkNotNull(roundId);
        checkNotNull(roll);
        if (map.get(roundId) != null) {
            throw new RuntimeException("There was already something in map!");
        }
        ImmutableRoundResult result = new ImmutableRoundResult(roll);
        System.out.println("The result of round " + roundId + " is "+result);
        map.put(roundId,result);
    }

    public boolean check(Selection selection, int stake, BetFuture betFuture) throws InterruptedException {
        //This is unfortunately to complicated because Triple constant is not in the Selection Enum
        RoundResult roundResult = map.get(betFuture.getRoundId());
        Integer expectedPrice = roundResult.calculatePrice(selection,stake);
        boolean check = expectedPrice.equals(betFuture.getPrize());
        if (!check) {
            System.out.println("Bet for round " + betFuture.getRoundId() + "should have been different...");
            System.out.println("Our selection: " + selection + " Round was: " + roundResult);
            System.out.println("We got: " + betFuture.getPrize() + " Should get: " + expectedPrice);
        }

        return check;
    }
}