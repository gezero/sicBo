package com.ingg.exercise.sicbo.solution;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Jiri
 */
public class SessionRandomGenerator implements RandomStringGenerator, RandomIntegerGenerator {
    private SecureRandom random = new SecureRandom();

    @Override
    public String generateString() {
        return new BigInteger(130, random).toString(32);
    }

    @Override
    public int generateInteger(int i) {
        return random.nextInt(i);
    }
}
