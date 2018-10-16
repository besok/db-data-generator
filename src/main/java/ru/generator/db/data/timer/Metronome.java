/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package ru.generator.db.data.timer;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * Class for generation  pauses by interrupted thread.
 *
 * */
public interface Metronome {

    void pause() throws InterruptedException;

/**
 * default implementation based on {@link LockSupport#parkNanos(long)}
 * */
    static Metronome parker(long period, TimeUnit unitMetric, Clock clock) {
        long periodInNanos = unitMetric.toNanos(period);
        return new Metronome() {

            private long next = clock.currentTimeInNanos() + periodInNanos;

            @Override
            public void pause() throws InterruptedException {
                while (next > clock.currentTimeInNanos()) {
                    LockSupport.parkNanos(next - clock.currentTimeInNanos());
                    if ( Thread.currentThread().isInterrupted() ) {
                        throw new InterruptedException();
                    }
                }
                next = next + periodInNanos;
            }

        };
    }
    static Metronome systemParker(long period, TimeUnit unitMetric) {
        return parker(period,unitMetric,Clock.system());
    }

}
