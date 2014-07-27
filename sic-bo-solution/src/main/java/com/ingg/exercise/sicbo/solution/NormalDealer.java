package com.ingg.exercise.sicbo.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Jiri
 */
public class NormalDealer implements Dealer, Runnable {

    RandomIntegerGenerator integerGenerator;
    private int timeout;
    private DealerObserver observer;
    private boolean execute = true;

    public NormalDealer(RandomIntegerGenerator integerGenerator, int timeout) {
        this.integerGenerator = integerGenerator;
        this.timeout = timeout;
    }

    @Override
    public synchronized Iterable<Integer> subscribe(DealerObserver observer) {
        if (this.observer != null) {
            throw new RuntimeException("You can subscribe only once");
        }
        this.observer =observer;
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
        return newRoll();
    }

    @Override
    public synchronized Iterable<Integer> stop() {
        execute = false;
        return newRoll();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (getExecute()) {
                observer.newRoll(newRoll());
            } else {
                return;
            }
        }
    }

    private synchronized Iterable<Integer> newRoll() {
        List<Integer> result = new ArrayList<>();
        result.add(integerGenerator.generateInteger(6)+1);
        result.add(integerGenerator.generateInteger(6)+1);
        result.add(integerGenerator.generateInteger(6)+1);
        return result;
    }

    public synchronized boolean getExecute() {
        return execute;
    }
}
