Cayenne Benchmarks
=

This small project contains some benchmarks of Cayenne's parts.

Run it:
```bash
mvn clean install
java -jar benchmark-4.X/target/benchmark-4.X.jar
```
where X can be 0, 1 or 2 

Also you can provide the size of collection to use in benchmarks through program arguments:
```bash
-jvmArgs -DobjectsNumber=SIZE
```

TODO
====
1. Provide argument to stub driver for defining dataset size.
2. Check stub driver for supporting all cayenne query types.