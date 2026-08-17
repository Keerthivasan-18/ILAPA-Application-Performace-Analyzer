# ILAPA — Intelligent Local Application Performance Analyzer

**ILAPA** is a lightweight desktop application for monitoring and analyzing the performance of locally running applications.

It observes resource consumption during a monitoring session and helps identify unusual application behaviour such as:

* Sudden CPU spikes
* Continuously increasing memory usage
* High disk activity
* Unusual network activity
* Resource usage that remains consistently high
* Performance degradation during a session

Instead of simply displaying raw system metrics, ILAPA attempts to turn collected performance data into a **simple performance analysis and score** that is easier to understand.

---

## 📌 Project Overview

When an application becomes slow, unresponsive, or resource-heavy, it can be difficult to determine what is happening internally.

Traditional operating-system monitoring tools provide large amounts of information, but they often require the user to manually interpret graphs, percentages, processes, and resource statistics.

ILAPA is designed as a lightweight developer-oriented performance analyzer that focuses on a single question:

> **"How is this application behaving on my local machine while I am using it?"**

The application monitors a selected running process and records performance information throughout a monitoring session.

At the end of the session, ILAPA analyzes the collected information and produces a rough performance assessment.

---

# 🎯 Goals

The primary goals of ILAPA are:

1. Monitor a running application's resource consumption.
2. Collect performance information at regular intervals.
3. Detect unusual resource behaviour.
4. Store monitoring sessions for later analysis.
5. Calculate a simple performance score.
6. Present information through an easy-to-understand JavaFX interface.
7. Keep the application lightweight enough to run on ordinary development machines.
8. Avoid requiring cloud services or external monitoring infrastructure.

---

# ✨ Features

## 🖥️ Application Monitoring

ILAPA can monitor a running local application and collect performance information during a monitoring session.

The monitoring session can include:

* Process CPU usage
* Process memory usage
* System disk activity
* System network activity
* Monitoring duration
* Detected performance anomalies

---

## ⚙️ CPU Monitoring

ILAPA observes the CPU consumption of the monitored process.

It can help identify:

* Sudden CPU spikes
* Sustained high CPU usage
* Unusual CPU behaviour
* CPU usage changes during a session

### Example

An application normally consuming around 5–15% CPU might suddenly consume 80–100%.

ILAPA can flag this as a potential CPU anomaly.

> **Implementation note:** CPU utilization requires comparing multiple samples. Therefore, the first CPU reading after starting a monitoring session may display `0%`. This is expected because there is not yet a previous sample available for comparison.

---

# 🧠 Memory Monitoring

ILAPA tracks the memory consumption of the monitored process.

It can identify patterns such as:

* Increasing memory usage
* Sudden memory growth
* Consistently high memory consumption
* Possible memory leaks

For example:

```text
100 MB
110 MB
125 MB
145 MB
170 MB
210 MB
260 MB
```

A continuous upward trend may indicate that the application is retaining memory over time.

ILAPA does **not** claim that increasing memory automatically means a memory leak. Instead, it treats the behaviour as a potential warning that deserves further investigation.

---

# 💾 Disk Monitoring

ILAPA monitors disk activity during the monitoring session.

This can help identify applications that generate unusually high disk activity.

Potential examples include:

* Applications repeatedly reading large files
* Applications continuously writing data
* Excessive logging
* Database-heavy operations
* File-processing workloads

Disk measurements are currently considered at the system level rather than being guaranteed to represent only the monitored process.

---

# 🌐 Network Monitoring

ILAPA also tracks network activity during a monitoring session.

Network usage can help provide additional context about what is happening while an application is running.

For example, high network activity could occur when an application is:

* Downloading data
* Uploading files
* Synchronizing information
* Communicating with an API
* Streaming content

### Current limitation

Network traffic is currently tracked **system-wide**, rather than strictly per process.

This is because OSHI and many cross-platform Java libraries do not directly expose reliable per-process network I/O statistics without using platform-specific native APIs or hooks.

Therefore:

> Network activity displayed by ILAPA represents the system's network activity and should not be interpreted as traffic generated exclusively by the monitored application.

---

# 🚨 Anomaly Detection

ILAPA does more than collect numbers.

It attempts to identify potentially unusual behaviour from the collected measurements.

Examples include:

### CPU Spike

```text
Normal CPU:
10%
12%
8%
15%

Detected:
92%  ← Potential CPU spike
```

### Memory Growth

```text
150 MB
165 MB
180 MB
205 MB
230 MB
260 MB
```

Potential continuous memory growth can be flagged for investigation.

### Sustained High Resource Usage

A process that consistently consumes large amounts of CPU or memory may also receive a warning.

---

# 📊 Performance Score

At the end of a monitoring session, ILAPA generates a **rough performance score**.

The score is intended to provide a quick overview rather than a scientifically standardized benchmark.

The score can take factors such as:

* CPU utilization
* Memory consumption
* Memory growth
* Disk activity
* Network activity
* Detected anomalies
* Overall stability during the session

into consideration.

### Example

```text
Performance Score
-----------------
82 / 100

CPU Usage        : Good
Memory Usage     : Good
Memory Trend     : Stable
Disk Activity    : Moderate
Network Activity : Moderate
Anomalies        : 1
```

The score should be interpreted as an **application-performance indicator**, not as an absolute measurement of application quality.

---

# 🏗️ Architecture

ILAPA follows a lightweight desktop architecture:

```text
                ┌─────────────────────┐
                │      JavaFX UI      │
                │                     │
                │ Dashboard / Charts  │
                │ Controls / Reports  │
                └──────────┬──────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │ Monitoring Service  │
                │                     │
                │ CPU / Memory / Disk │
                │ Network Collection  │
                └──────────┬──────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │    OSHI Layer       │
                │                     │
                │ OS / Process Stats  │
                └──────────┬──────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │ Analysis Engine     │
                │                     │
                │ Trend Detection     │
                │ Anomaly Detection   │
                │ Performance Score   │
                └──────────┬──────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │     SQLite DB       │
                │                     │
                │ Sessions / Samples  │
                │ Analysis Results    │
                └─────────────────────┘
```

---

# 🧰 Technology Stack

| Technology   | Purpose                                 |
| ------------ | --------------------------------------- |
| **Java 21**  | Core application language               |
| **JavaFX**   | Desktop graphical user interface        |
| **OSHI**     | Operating-system and process monitoring |
| **SQLite**   | Local performance-data storage          |
| **Maven**    | Dependency management and build system  |
| **jpackage** | Desktop application packaging           |

---

# ☕ Why Java?

Java was selected because it provides:

* Strong cross-platform support
* Mature ecosystem
* Excellent filesystem and process APIs
* Good performance
* Long-term maintainability
* Easy integration with SQLite
* Compatibility with OSHI
* JavaFX support for desktop interfaces

Java 21 also provides a modern LTS development environment.

---

# 🖼️ Why JavaFX?

JavaFX provides the graphical interface required for ILAPA.

It can be used for:

* Dashboards
* Tables
* Charts
* Progress indicators
* Monitoring controls
* Performance reports
* Real-time metric updates

The goal is to provide a visual monitoring experience without requiring a browser or external server.

---

# 🔍 Why OSHI?

[OSHI](https://github.com/oshi/oshi) provides Java-based access to operating-system information.

ILAPA uses OSHI to obtain information related to:

* Processes
* CPU
* Memory
* Disk
* Network
* Operating-system statistics

This allows ILAPA to interact with local system information without implementing every platform-specific monitoring mechanism from scratch.

---

# 🗄️ Data Storage

ILAPA uses **SQLite** for local persistence.

The database is automatically created when the application is first started.

Default database location:

```text
database/performance.db
```

The database can contain information such as:

* Monitoring sessions
* Process information
* Performance samples
* CPU measurements
* Memory measurements
* Disk measurements
* Network measurements
* Detected anomalies
* Performance scores

Because SQLite is embedded, ILAPA does not require a separate database server.

---

# 📁 Project Structure

A typical project structure is:

```text
ILAPA/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── ilapa/
│       │           ├── Main.java
│       │           │
│       │           ├── controller/
│       │           ├── service/
│       │           ├── model/
│       │           ├── repository/
│       │           ├── analyzer/
│       │           └── util/
│       │
│       └── resources/
│           ├── fxml/
│           ├── css/
│           └── images/
│
├── database/
│   └── performance.db
│
├── pom.xml
└── README.md
```

The exact package structure may evolve as the project grows.

---

# 🔄 Monitoring Workflow

A typical monitoring session follows this process:

```text
Start ILAPA
     │
     ▼
Select Running Application
     │
     ▼
Start Monitoring
     │
     ▼
Collect Performance Samples
     │
     ├── CPU
     ├── Memory
     ├── Disk
     └── Network
     │
     ▼
Store Samples
     │
     ▼
Analyze Trends
     │
     ▼
Detect Anomalies
     │
     ▼
Calculate Performance Score
     │
     ▼
Display Results
```

---

# 🧪 Example Monitoring Session

Suppose a user monitors an application for five minutes.

During the session, ILAPA observes:

```text
CPU:
12% → 15% → 13% → 18% → 91%

Memory:
180 MB → 185 MB → 190 MB → 205 MB → 245 MB

Disk:
Low → Low → Moderate → High → Moderate

Network:
Moderate → Moderate → High → High → Low
```

ILAPA could report:

```text
Potential Issues
----------------
⚠ CPU spike detected
⚠ Memory increased continuously
ℹ High network activity observed
```

The user can then investigate the application using a debugger, profiler, logs, or other development tools.

---

# 🧮 Performance Analysis Philosophy

ILAPA is not intended to replace professional profilers such as:

* Java Flight Recorder
* VisualVM
* YourKit
* JProfiler
* Operating-system performance tools

Instead, ILAPA focuses on providing a **quick, lightweight first-level diagnosis**.

The idea is:

```text
Raw System Metrics
       ↓
ILAPA Analysis
       ↓
Simple Warnings
       ↓
Performance Score
       ↓
Developer Investigation
```

ILAPA tells the developer:

> "Something unusual may be happening here."

The developer can then use specialized profiling tools to determine exactly why.

---

# 🚀 Getting Started

## Prerequisites

Make sure the following are installed:

* Java 21 or later
* Maven
* Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

---

# 📥 Clone the Repository

```bash
git clone <repository-url>
cd ILAPA
```

---

# ▶️ Run the Application

Use Maven:

```bash
mvn clean javafx:run
```

On the first run, ILAPA automatically creates:

```text
database/performance.db
```

---

# 🔨 Build the Project

Create the packaged JAR:

```bash
mvn clean package
```

The resulting JAR will be available inside:

```text
target/
```

---

# 📦 Packaging With jpackage

ILAPA can be packaged as a native desktop application using Java's `jpackage`.

Example:

```bash
jpackage \
  --input target/ \
  --main-jar ilapa-1.0.0.jar \
  --main-class com.ilapa.Main \
  --name ILAPA
```

The generated package can then be distributed as a desktop application.

The exact packaging configuration may need to be adjusted depending on the target operating system.

---

# 🖥️ Cross-Platform Considerations

ILAPA is designed with cross-platform support in mind.

Potential target platforms include:

* Windows
* Linux
* macOS

However, some monitoring capabilities can behave differently between operating systems.

In particular:

* Process information may vary.
* Disk statistics may vary.
* Network statistics may vary.
* Process permissions may affect visibility.
* Some OS-specific metrics may require additional native support.

Therefore, ILAPA should treat operating-system-specific information carefully rather than assuming that every metric behaves identically everywhere.

---

# ⚠️ Current Limitations

## 1. Network Monitoring Is System-Wide

Network usage currently represents system-level activity.

It cannot reliably determine:

```text
Application A → 25 MB
Application B → 10 MB
```

using only the current cross-platform monitoring approach.

Per-process network monitoring would require platform-specific mechanisms or native hooks.

---

## 2. First CPU Reading Is 0%

CPU utilization requires at least two measurements.

For example:

```text
Sample 1 → baseline
Sample 2 → comparison
```

Therefore, the first reading after pressing **Start** may show:

```text
CPU: 0%
```

This is expected behaviour and does not indicate that the application is consuming zero CPU.

---

## 3. Performance Score Is Approximate

The ILAPA score is intended as a convenient indicator.

It is not:

* A standardized benchmark
* A replacement for profiling
* A guarantee of application quality
* A universal comparison between different applications

Different applications naturally have different resource requirements.

For example, a video encoder consuming high CPU may be behaving normally, while a simple text editor consuming the same amount could be suspicious.

---

## 4. Resource Usage Depends on Workload

Performance measurements depend heavily on what the application is doing.

A process may legitimately consume:

```text
High CPU
```

during a compilation, rendering operation, or computation-heavy task.

Therefore, ILAPA should interpret measurements in terms of **patterns and trends**, not simply label every high value as a problem.

---

# 🔐 Privacy

ILAPA is designed as a local monitoring application.

Performance information is stored locally using SQLite.

The project does not require sending monitoring data to a remote server for its core functionality.

The monitored information is intended to remain on the user's machine unless the user explicitly chooses to export or share it.

---

# 🧩 Future Improvements

Possible future versions could introduce:

## Per-Process Network Monitoring

Implement native operating-system integrations to determine exactly how much network traffic is generated by each process.

---

## Advanced Memory-Leak Detection

Instead of simply detecting memory growth, future versions could analyze:

* Long-term memory trends
* Allocation patterns
* Garbage-collection behaviour
* Memory stabilization
* Repeated growth cycles

---

## Historical Session Comparison

Allow users to compare sessions:

```text
Session #1
CPU: 21%
Memory: 180 MB

Session #2
CPU: 27%
Memory: 250 MB

Difference:
CPU +6%
Memory +70 MB
```

This would make ILAPA useful for regression testing.

---

## Performance Regression Detection

Future versions could compare a new session against previous sessions and automatically detect degradation.

Example:

```text
Previous average memory:
180 MB

Current average memory:
260 MB

⚠ Possible performance regression
```

---

## Export Reports

Add support for exporting monitoring results as:

* CSV
* JSON
* PDF
* HTML

---

## Real-Time Charts

Provide live graphs for:

```text
CPU
Memory
Disk
Network
```

instead of relying only on numerical values.

---

## Application Profiles

Different applications could have different expected resource profiles.

For example:

```text
Web Browser
Expected:
High memory
Moderate CPU

Text Editor
Expected:
Low memory
Low CPU
```

This could make anomaly detection more context-aware.

---

## Alert System

Future versions could notify users when a significant anomaly is detected.

Example:

```text
⚠ ILAPA Alert

CPU usage exceeded 90%
for 15 consecutive seconds.
```

---

# 🛠️ Possible Advanced Architecture

As ILAPA grows, the architecture could evolve into:

```text
                    ┌──────────────────┐
                    │    JavaFX UI     │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │ Monitoring API   │
                    └────────┬─────────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ CPU      │   │ Memory   │   │ I/O      │
        │ Monitor  │   │ Monitor  │   │ Monitor  │
        └────┬─────┘   └────┬─────┘   └────┬─────┘
             │              │              │
             └──────────────┼──────────────┘
                            ▼
                   ┌─────────────────┐
                   │ Sample Storage  │
                   └────────┬────────┘
                            ▼
                   ┌─────────────────┐
                   │ Analysis Engine │
                   └────────┬────────┘
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
        ┌──────────────┐       ┌──────────────┐
        │ Anomaly      │       │ Performance  │
        │ Detection    │       │ Scoring      │
        └──────────────┘       └──────────────┘
```

This separation makes it easier to add new monitoring sources without rewriting the entire application.

---

# 📈 What Makes ILAPA Different?

Traditional system monitors primarily answer:

> "How much CPU or memory is being used?"

ILAPA aims to answer a slightly different question:

> **"Is this application's behaviour unusual, and should I investigate it?"**

The project therefore focuses on combining:

```text
Monitoring
    +
Data Collection
    +
Trend Analysis
    +
Anomaly Detection
    +
Performance Scoring
```

into a single lightweight desktop application.

---

# 🎓 Learning Objectives

ILAPA is also designed as a practical software-engineering project covering several important areas:

### Java

* Object-oriented programming
* Collections
* Multithreading
* Exception handling
* File handling
* JDBC
* Application architecture

### JavaFX

* UI development
* Controllers
* FXML
* Charts
* Event handling
* Background tasks

### Operating Systems

* Processes
* CPU scheduling concepts
* Memory usage
* Disk I/O
* Network interfaces
* System resource monitoring

### Databases

* SQLite
* JDBC
* Database schema design
* Querying performance data
* Historical data storage

### Software Engineering

* Layered architecture
* Separation of concerns
* Monitoring services
* Data analysis
* Error handling
* Packaging and distribution

---

# 🧪 Testing Strategy

ILAPA should be tested against different application workloads.

Examples include:

### CPU-intensive application

```text
Expected:
CPU usage increases significantly.
```

### Memory-intensive application

```text
Expected:
Memory usage increases.
```

### Disk-intensive application

```text
Expected:
Disk activity increases.
```

### Network-intensive application

```text
Expected:
System network activity increases.
```

### Idle application

```text
Expected:
Low and relatively stable resource usage.
```

Testing different workloads helps determine whether ILAPA correctly distinguishes normal behaviour from unusual behaviour.

---

# 📋 Example Final Report

A future ILAPA monitoring report could look like:

```text
==================================================
                 ILAPA REPORT
==================================================

Application:
Example Application

Monitoring Duration:
10 minutes

--------------------------------------------------
CPU
--------------------------------------------------
Average:       24%
Peak:          87%
Status:        Moderate
Anomalies:     1

--------------------------------------------------
MEMORY
--------------------------------------------------
Starting:      180 MB
Ending:        245 MB
Peak:          260 MB
Trend:         Increasing
Status:        Warning

--------------------------------------------------
DISK
--------------------------------------------------
Activity:      Moderate
Status:        Normal

--------------------------------------------------
NETWORK
--------------------------------------------------
Activity:      High
Scope:         System-wide

--------------------------------------------------
PERFORMANCE SCORE
--------------------------------------------------

              74 / 100

Overall Status:
Moderate

Warnings:
⚠ CPU spike detected
⚠ Memory growth detected

==================================================
```

---

# 🚧 Project Status

**Current status:** Active development

Currently implemented concepts include:

* Local application monitoring
* CPU monitoring
* Memory monitoring
* Disk monitoring
* System-wide network monitoring
* SQLite persistence
* Basic anomaly detection
* Performance scoring
* JavaFX desktop interface

Additional functionality can be introduced incrementally as the project evolves.

---

# 🤝 Contributing

Contributions and improvements are welcome.

A typical contribution workflow is:

```bash
git checkout -b feature/new-monitor
```

Make your changes, test them locally, and then create a pull request.

Potential contribution areas include:

* New monitoring metrics
* Better anomaly detection
* UI improvements
* Database optimization
* Cross-platform support
* Testing
* Performance improvements
* Report generation

---

# 📄 License

Add the project's chosen license here.

For example:

```text
MIT License
```

---

# 👨‍💻 Project

**ILAPA — Intelligent Local Application Performance Analyzer**

A lightweight local performance monitoring and analysis tool built with:

```text
Java 21
JavaFX
OSHI
SQLite
Maven
```

The long-term objective is to evolve ILAPA from a simple resource monitor into a practical **local application performance diagnosis tool** that helps developers quickly identify suspicious resource behaviour before moving to more advanced profiling tools.
