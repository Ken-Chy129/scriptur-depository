package cn.ken;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Ken-Chy129
 * @date 2025/5/18
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class AvoidGetFieldBenchmark {

    public static class AvoidGetFieldState {
        public int a;

        public AvoidGetFieldState() {
        }

        public AvoidGetFieldState(int a) {
            this.a = a;
        }
    }

    @Benchmark
    public void testWithGetField() {
        AvoidGetFieldState state = new AvoidGetFieldState(1);
        state.a += 1;
        state.a += 2;
        state.a += 3;
        state.a += 4;
        state.a += 5;
    }

    @Benchmark
    public void testAvoidGetField() {
        AvoidGetFieldState state = new AvoidGetFieldState(1);
        int a = state.a;
        a += 1;
        a += 2;
        a += 3;
        a += 4;
        a += 5;
        state.a = a;
    }


}
