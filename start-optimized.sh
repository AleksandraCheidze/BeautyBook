#!/bin/bash

echo "Starting BeautyBook with optimized JVM settings..."

# JVM Memory Settings
HEAP_MIN="512m"
HEAP_MAX="1g"
METASPACE_MAX="256m"

# GC Settings (G1GC optimized for low latency)
GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=8 -XX:InitiatingHeapOccupancyPercent=45"

# JIT Compiler optimizations
JIT_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=4 -XX:+UseStringDeduplication"

# Memory optimizations
MEM_OPTS="-XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:CompressedClassSpaceSize=128m"

# JMX Monitoring
JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

# GC Logging (for monitoring)
GC_LOG_OPTS="-Xlog:gc*:logs/gc.log:time,tags -Xlog:safepoint:logs/safepoint.log:time,tags"

# Performance monitoring
PERF_OPTS="-XX:+FlightRecorder -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints"

# Application-specific optimizations
APP_OPTS="-Dspring.jmx.enabled=true -Dspring.application.admin.enabled=true -Djava.awt.headless=true -Dfile.encoding=UTF-8"

# Combine all options
JVM_OPTS="-Xms${HEAP_MIN} -Xmx${HEAP_MAX} -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=${METASPACE_MAX} ${GC_OPTS} ${JIT_OPTS} ${MEM_OPTS} ${JMX_OPTS} ${GC_LOG_OPTS} ${PERF_OPTS} ${APP_OPTS}"

echo "JVM Options: ${JVM_OPTS}"
echo

# Create logs directory if it doesn't exist
mkdir -p logs

# Start the application
java ${JVM_OPTS} -jar target/BeautyBook-3.2.1.jar
