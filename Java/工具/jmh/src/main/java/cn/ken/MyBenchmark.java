package cn.ken;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * @author Ken-Chy129
 * @date 2025/5/18
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Threads(4)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MyBenchmark {

    @Benchmark
    public void measureSimpleMath(Blackhole blackhole) {
        // 基准代码
        blackhole.consume(add(1, 2));
    }

    private int add(int a, int b) {
        return a + b;
    }
}
