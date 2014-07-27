package com.ingg.exercise.sicbo.solution.endtoend;

import com.ingg.exercise.sicbo.model.BetFuture;
import com.ingg.exercise.sicbo.model.Selection;
import com.ingg.exercise.sicbo.model.exception.TableClosedException;
import com.ingg.exercise.sicbo.solution.ProvablyFairBetFuture;
import com.ingg.exercise.sicbo.solution.ProvablyFairResult;
import com.ingg.exercise.sicbo.solution.ProvablyFairSicBo;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Jiri
 */
public class ProveOfFairness {

    @Test
    public void testProveOfFairness() throws Exception {
        ProvablyFairSicBo provablyFairSicBo = new ProvablyFairSicBo(new ResultGatherer());

        provablyFairSicBo.open();

        String currentRoundId = provablyFairSicBo.getCurrentRoundId();

        ProvablyFairBetFuture betFuture = provablyFairSicBo.acceptBet(currentRoundId, Selection.BIG, 10);
        assertThat(betFuture.getRoundId(),is(currentRoundId));

        provablyFairSicBo.close();

        Iterable<Integer> roll = betFuture.getRoll();
        String salt = betFuture.getSalt();

        String digest = calculateDigest(roll, salt);
        assertThat(digest,is(betFuture.getRoundId()));

    }

    private String calculateDigest(Iterable<Integer> roll, String salt) {
        StringBuilder builder = new StringBuilder();
        for (Integer integer : roll) {
            builder.append(integer);
        }
        String currentRoundId;
        builder.append(salt);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            BigInteger bigInt = new BigInteger(1, digest.digest(builder.toString().getBytes()));
            currentRoundId = bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA 256 was not found, now that is strange....",e);
        }
        return currentRoundId;
    }
}
