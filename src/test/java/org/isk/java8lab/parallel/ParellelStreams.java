package org.isk.java8lab.parallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ParellelStreams {
  @Test
  public void commonPool() {
    ForkJoinPool commonPool = ForkJoinPool.commonPool();
    System.out.println(commonPool.getParallelism()); // 3 | -Djava.util.concurrent.ForkJoinPool.common.parallelism=5
  }

  // In order to understate the parallel execution behavior of a parallel stream the next example prints information
  // about the current thread to sout:
  @Test
  public void example_1() {
    Arrays.asList("a1", "a2", "b1", "c2", "c1").parallelStream().filter(s -> {
      System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName());
      return true;
    }).map(s -> {
      System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName());
      return s.toUpperCase();
    }).forEach(s -> System.out.format("forEach: %s [%s]\n", s, Thread.currentThread().getName()));
  }

  // As you can see the parallel stream utilizes all available threads from the common ForkJoinPool for executing the
  // stream operations. The output may differ in consecutive runs because the behavior which particular thread is
  // actually used is non-deterministic.
  // Let's extend the example by an additional stream operation, sort:
  @Test
  public void example_2() {
    Arrays.asList("a1", "a2", "b1", "c2", "c1").parallelStream().filter(s -> {
      System.out.format("filter: %s [%s]\n", s, Thread.currentThread().getName());
      return true;
    }).map(s -> {
      System.out.format("map: %s [%s]\n", s, Thread.currentThread().getName());
      return s.toUpperCase();
    }).sorted((s1, s2) -> {
      System.out.format("sort: %s <> %s [%s]\n", s1, s2, Thread.currentThread().getName());
      return s1.compareTo(s2);
    }).forEach(s -> System.out.format("forEach: %s [%s]\n", s, Thread.currentThread().getName()));
  }

  // It seems that sort is executed sequentially on the main thread only. But this is not the case. Actually, sort on a
  // parallel stream uses the new Java 8 method Arrays.parallelSort() under the hood. Keep in mind that the debug output
  // only refers to the execution of the passed lambda expression. So, for sort the comparator is executed solely on the
  // main thread but the actual sorting algorithm is executed in parallel.

  @Test
  public void sequential() {
    List<String> values = this.generateValues();

    long t0 = System.nanoTime();

    long count = values.stream().sorted().count();
    System.out.println(count);

    long t1 = System.nanoTime();

    long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
    System.out.println(String.format("sequential sort took: %d ms", millis));
  }

  @Test
  public void parallel() {
    List<String> values = this.generateValues();

    long t0 = System.nanoTime();

    long count = values.parallelStream().sorted().count();
    System.out.println(count);

    long t1 = System.nanoTime();

    long millis = TimeUnit.NANOSECONDS.toMillis(t1 - t0);
    System.out.println(String.format("parallel sort took: %d ms", millis));
  }

  private List<String> generateValues() {
    int max = 1000000;
    List<String> values = new ArrayList<>(max);
    for (int i = 0; i < max; i++) {
      UUID uuid = UUID.randomUUID();
      values.add(uuid.toString());
    }
    return values;
  }
}
