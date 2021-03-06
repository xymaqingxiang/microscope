// This file is part of OpenTSDB.
// Copyright (C) 2012  The OpenTSDB Authors.
//
// This program is free software: you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or (at your
// option) any later version.  This program is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
// General Public License for more details.  You should have received a copy
// of the GNU Lesser General Public License along with this program.  If not,
// see <http://www.gnu.org/licenses/>.
package net.opentsdb.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public final class TestAggregators {

  private static final Random random;
  static {
    final long seed = System.nanoTime();
    System.out.println("Random seed: " + seed);
    random = new Random(seed);
  }

  /**
   * Epsilon used to compare floating point values.
   * Instead of using a fixed epsilon to compare our numbers, we calculate
   * it based on the percentage of our actual expected values.  We do things
   * this way because our numbers can be extremely large and if you change
   * the scale of the numbers a static precision may no longer work
   */
  private static final double EPSILON_PERCENTAGE = 0.0001;

  /** Helper class to hold a bunch of numbers we can iterate on.  */
  private static final class Numbers implements Aggregator.Longs, Aggregator.Doubles {
    private final long[] numbers;
    private int i = 0;

    public Numbers(final long[] numbers) {
      this.numbers = numbers;
    }

    @Override
    public boolean hasNextValue() {
      return i < numbers.length;
    }

    @Override
    public long nextLongValue() {
      return numbers[i++];
    }

    @Override
    public double nextDoubleValue() {
      return numbers[i++];
    }

    void reset() {
      i = 0;
    }
  }

  @Test
  public void testStdDevKnownValues() {
    final long[] values = new long[10000];
    for (int i = 0; i < values.length; i++) {
      values[i] = i;
    }
    // Expected value calculated by NumPy
    // $ python2.7
    // >>> import numpy
    // >>> numpy.std(range(10000))
    // 2886.7513315143719
    final double expected = 2886.7513315143719D;
    final double epsilon = 0.01;
    checkSimilarStdDev(values, expected, epsilon);
  }

  @Test
  public void testStdDevRandomValues() {
    final long[] values = new long[1000];
    for (int i = 0; i < values.length; i++) {
      values[i] = random.nextLong();
    }
    final double expected = naiveStdDev(values);
    // Calculate the epsilon based on the percentage of the number.
    final double epsilon = EPSILON_PERCENTAGE * expected;
    checkSimilarStdDev(values, expected, epsilon);
  }

  @Test
  public void testStdDevNoDeviation() {
    final long[] values = {3,3,3};

    final double expected = 0;
    checkSimilarStdDev(values, expected, 0);
  }

  @Test
  public void testStdDevFewDataInputs() {
    final long[] values = {1,2};

    final double expected = 0.5;
    checkSimilarStdDev(values, expected, 0);
  }

  private static void checkSimilarStdDev(final long[] values,
                                         final double expected,
                                         final double epsilon) {
    final Numbers numbers = new Numbers(values);
    final Aggregator agg = Aggregators.get("dev");

    Assert.assertEquals(expected, agg.runDouble(numbers), epsilon);
    numbers.reset();
    Assert.assertEquals(expected, agg.runLong(numbers), Math.max(epsilon, 1.0));
  }

  private static double naiveStdDev(long[] values) {
    double sum = 0;
    for (final double value : values) {
      sum += value;
    }
    double mean = sum / values.length;

    double squaresum = 0;
    for (final double value : values) {
      squaresum += Math.pow(value - mean, 2);
    }
    final double variance = squaresum / values.length;
    return Math.sqrt(variance);
  }

}
