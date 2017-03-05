package pl.koziolekweb;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import static pl.koziolekweb.Params.*;


/**
 * Created by BKuczynski on 2017-03-05.
 */
public class LongBenchmark {

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void loopWithSum(Blackhole blackhole) {
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
    public void streamWithSum(Blackhole blackhole) {
        blackhole.consume(LongStream.range(0, MAX).sum());
    }

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void pStreamWithSum(Blackhole blackhole) {
        blackhole.consume(LongStream.range(0, MAX).parallel().sum());
    }

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void fjWithSum(Blackhole blackhole) {
        ForkJoinPool pool = new ForkJoinPool();
        blackhole.consume(pool.invoke(new BigIntFJT(0, MAX)));
    }
}


class LongFJT extends RecursiveTask<Long> {

    private final int start;
    private final int end;
    private final int MAX = 1_000;


    public LongFJT(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= MAX) {
            long sum = 0L;

            for (int i = 0; i < MAX; i++) {
                sum += i;
            }

            return sum;
        }
        LongFJT fjt1 = new LongFJT(start, start + ((end - start) / 2));
        LongFJT fjt2 = new LongFJT(start + ((end - start) / 2), end);

        fjt1.fork();
        fjt2.fork();
        return fjt1.join() + fjt2.join();
    }
}