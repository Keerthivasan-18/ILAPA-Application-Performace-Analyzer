# ILAPA - Intelligent Local Application Performance Analyzer

Desktop app that watches a running application's CPU, memory, disk and network
usage over a monitoring session, flags weird behaviour (CPU spikes, memory that
keeps climbing) and gives the session a rough performance score.

## Stack
Java 21, JavaFX, OSHI (for reading OS/process stats), SQLite.

## Running it
```
mvn clean javafx:run
```
First run will create a `database/performance.db` file automatically.

## Packaging
```
mvn clean package
jpackage --input target/ --main-jar ilapa-1.0.0.jar --main-class com.ilapa.Main --name ILAPA
```

## Notes
- Network usage is tracked system wide, not per process — OSHI (and most
  cross platform libraries) don't expose per-process network I/O without
  native hooks, so this is a known limitation for now.
- CPU load needs two samples to compare against each other, so the very first
  reading after you hit Start will show 0%, that's expected.
