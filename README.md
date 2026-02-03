# TrainService

Simple Java project (no build tool). Source lives in `src/`, tests in `test/`.

## Compile

macOS/Linux:

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out | Out-Null
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -d out @sources.txt
```

## Run main

```bash
java -cp out com.trainservice.TrainService
```

## Run tests

macOS/Linux:

```bash
mkdir -p out
find src test -name "*.java" > sources.txt
javac -cp lib/junit-platform-console-standalone.jar -d out @sources.txt
java -jar lib/junit-platform-console-standalone.jar -cp out --scan-classpath
```

Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out | Out-Null
Get-ChildItem -Recurse -Filter *.java src, test | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -cp lib/junit-platform-console-standalone.jar -d out @sources.txt
java -jar lib/junit-platform-console-standalone.jar -cp out --scan-classpath
```
