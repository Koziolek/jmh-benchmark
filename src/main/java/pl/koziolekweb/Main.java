package pl.koziolekweb;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Created by BKuczynski on 2017-05-16.
 */
public class Main {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BoxingBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
