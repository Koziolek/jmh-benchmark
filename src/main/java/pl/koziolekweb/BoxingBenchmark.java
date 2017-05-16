package pl.koziolekweb;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static pl.koziolekweb.Params.LOOP;
import static pl.koziolekweb.Params.MAX;
import static pl.koziolekweb.Params.TIME;

/**
 * Created by BKuczynski on 2017-05-16.
 */
public class BoxingBenchmark {

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void loopWithPrimitiveSum(Blackhole blackhole) {
        long sum = 0L;

        for (int i = 0; i < MAX; i++) {
            sum += i;
        }
        blackhole.consume(sum);
    }

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void loopWithBoxedSum(Blackhole blackhole) {
        Long sum = 0L;

        for (int i = 0; i < MAX; i++) {
            sum += i;
        }
        blackhole.consume(sum);
    }


}
