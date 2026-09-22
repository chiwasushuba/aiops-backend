---
name: aiops-performance
description: Investigate or improve measured latency, throughput, memory, or database cost in this AIOps backend. Use only when performance is requested or there is a concrete symptom.
---

# Performance work

Record the slow operation, workload, baseline, and measurement method before
changing code or configuration. Locate the bottleneck with evidence such as
request timing, query counts or plans, JVM metrics, or profiling. Check
pagination and database access for the affected endpoint before introducing
caches, parallelism, or new infrastructure.

Change one cause at a time and compare the same workload before and after.
Preserve API behavior and data correctness, and report the measured result and
its limits. Do not assume this project has model inference or telemetry
pipelines until those components exist.
