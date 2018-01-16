This project is a test around using json path to support arrays of messages in Apache Metron's JSONMap parser.

Without Caching
```bash
# Run complete. Total time: 00:20:22

Benchmark                     Mode  Cnt      Score      Error  Units
AppPerfTest.many              avgt  200  18655.304 ± 1075.530  ns/op
AppPerfTest.single            avgt  200   2114.112 ±   12.875  ns/op
AppPerfTest.singleThreeTimes  avgt  200   6491.127 ±   96.099  ns/op
```


With Caching
```bash

# Run complete. Total time: 00:20:21

Benchmark                     Mode  Cnt      Score     Error  Units
AppPerfTest.many              avgt  200  16409.461 ± 157.901  ns/op
AppPerfTest.single            avgt  200   2143.481 ±  29.056  ns/op
AppPerfTest.singleThreeTimes  avgt  200   6229.745 ±  50.818  ns/op
```