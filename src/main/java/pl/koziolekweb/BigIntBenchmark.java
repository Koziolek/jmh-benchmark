/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.koziolekweb;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static pl.koziolekweb.Params.*;

public class BigIntBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void loopWithSum(Blackhole blackhole) {
        BigInteger sum = new BigInteger("0");

        for (int i = 0; i < MAX; i++) {
            sum = sum.add(new BigInteger(i + ""));
        }
        blackhole.consume(sum);
    }

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void streamWithSum(Blackhole blackhole) {
        blackhole.consume(IntStream.range(0, MAX)
                .mapToObj(i -> new BigInteger(i + ""))
                .reduce(new BigInteger("0"), BigInteger::add));
    }

    @Benchmark
    @Warmup(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = LOOP, time = TIME, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(LOOP)
    public void pStreamWithSum(Blackhole blackhole) {
        blackhole.consume(IntStream.range(0, MAX).parallel()
                .mapToObj(i -> new BigInteger(i + ""))
                .reduce(new BigInteger("0"), BigInteger::add));
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


class BigIntFJT extends RecursiveTask<BigInteger> {

    private final int start;
    private final int end;
    private final int MAX = 1_000;


    public BigIntFJT(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected BigInteger compute() {
        if (end - start <= MAX) {
            BigInteger sum = new BigInteger("0");

            for (int i = start; i < end; i++) {
                sum = sum.add(new BigInteger(i + ""));
            }

            return sum;
        }
        BigIntFJT fjt1 = new BigIntFJT(start, start + ((end - start) / 2));
        BigIntFJT fjt2 = new BigIntFJT(start + ((end - start) / 2), end);

        fjt1.fork();
        fjt2.fork();
        return fjt1.join().add(fjt2.join());
    }
}