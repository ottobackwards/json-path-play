This project is a test around using json path to support arrays of messages in Apache Metron's JSONMap parser.

```bash
# Run complete. Total time: 00:20:22

Benchmark                     Mode  Cnt      Score      Error  Units
AppPerfTest.many              avgt  200  18655.304 ± 1075.530  ns/op
AppPerfTest.single            avgt  200   2114.112 ±   12.875  ns/op
AppPerfTest.singleThreeTimes  avgt  200   6491.127 ±   96.099  ns/op
```
