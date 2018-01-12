package org.ottobackwards;

import java.util.concurrent.TimeUnit;
import org.adrianwalker.multilinestring.Multiline;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@OperationsPerInvocation(1)
public class AppPerfTest {

  static App app = new App();

  /**
   * {
   * "prop1" : "value1",
   * "prop2" : "value2",
   * "prop3" : "value3",
   * "prop4" : "value4",
   * "prop5" : "value5",
   * "prop6" : "value6",
   * "prop7" : "value7",
   * "prop8" : "value8",
   * "prop9" : "value9",
   * "prop10" : "value10"
   * }
   */
  @Multiline
  static String one;


  /**
   * {
   * "occurrences" : [
   * {
   * "prop1" : "value1",
   * "prop2" : "value2",
   * "prop3" : "value3",
   * "prop4" : "value4",
   * "prop5" : "value5",
   * "prop6" : "value6",
   * "prop7" : "value7",
   * "prop8" : "value8",
   * "prop9" : "value9",
   * "prop10" : "value10"
   * },
   * {
   * "prop1" : "value11",
   * "prop2" : "value21",
   * "prop3" : "value31",
   * "prop4" : "value41",
   * "prop5" : "value51",
   * "prop6" : "value61",
   * "prop7" : "value71",
   * "prop8" : "value81",
   * "prop9" : "value91",
   * "prop10" : "value11"
   * },
   * {
   * "prop1" : "value12",
   * "prop2" : "value22",
   * "prop3" : "value32",
   * "prop4" : "value42",
   * "prop5" : "value52",
   * "prop6" : "value62",
   * "prop7" : "value72",
   * "prop8" : "value82",
   * "prop9" : "value92",
   * "prop10" : "value12"
   * }
   * ]
   * }
   */
  @Multiline
  static String many;


  @Benchmark
  public void single() {
    app.parse(one.getBytes());
  }

  @Benchmark
  public void singleThreeTimes() {
    app.parse(one.getBytes());
    app.parse(one.getBytes());
    app.parse(one.getBytes());
  }

  @Benchmark
  public void many() {
    app.parserMany(many.getBytes(), "$.occurrences");
  }

}
