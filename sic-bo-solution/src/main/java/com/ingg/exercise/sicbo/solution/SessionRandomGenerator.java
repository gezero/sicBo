package com.ingg.exercise.sicbo.solution;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Jiri
 */
public class SessionRandomGenerator implements RandomStringGenerator {
    private SecureRandom random = new SecureRandom();

    @Override
    public String generateString() {
        return new BigInteger(130, random).toString(32);
    }
}
