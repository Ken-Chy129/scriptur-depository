package cn.ken;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Ken-Chy129
 * @date 2025/5/18
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class StringConcatBenchmark {

    @Benchmark
    public String concatByPlus() {
        String str = "";
        for (int i = 0; i < 100; i++) {
            str += i;
        }
        return str;
    }

    @Benchmark
    public String concatByStringBuilder() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            str.append(i);
        }
        return str.toString();
    }

    @Benchmark
    public String concatByStringBuffer() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            str.append(i);
        }
        return str.toString();
    }
}
