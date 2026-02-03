# TrainService

Simple Java project (no build tool). Source lives in `src/`, tests in `test/`.

## Compile

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
```

## Run main

```bash
java -cp out com.trainservice.TrainService
```

## Run tests

```bash
mkdir -p out
javac -cp lib/junit-platform-console-standalone.jar -d out $(find src test -name "*.java")
java -jar lib/junit-platform-console-standalone.jar -cp out --scan-classpath
```
