#!/bin/bash

echo "Starting BeautyBook with Railway-optimized JVM settings..."

# Railway-optimized JVM Memory Settings (reduced for 512MB limit)
HEAP_MIN="128m"
HEAP_MAX="384m"
METASPACE_MAX="96m"

# GC Settings optimized for low memory usage
GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:G1HeapRegionSize=8m -XX:G1NewSizePercent=20 -XX:G1MaxNewSizePercent=30 -XX:InitiatingHeapOccupancyPercent=35"

# JIT Compiler optimizations for faster startup
JIT_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Memory optimizations for Railway
MEM_OPTS="-XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:CompressedClassSpaceSize=64m -XX:+UseStringDeduplication"

# Reduced monitoring for production
MONITORING_OPTS="-XX:+UnlockDiagnosticVMOptions -XX:+LogVMOutput"

# Application-specific optimizations
APP_OPTS="-Dspring.jmx.enabled=false -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"

# Combine all options
JVM_OPTS="-Xms${HEAP_MIN} -Xmx${HEAP_MAX} -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=${METASPACE_MAX} ${GC_OPTS} ${JIT_OPTS} ${MEM_OPTS} ${MONITORING_OPTS} ${APP_OPTS}"

echo "Railway JVM Options: ${JVM_OPTS}"
echo

# Start the application
java ${JVM_OPTS} -Dspring.profiles.active=production -Dserver.port=${PORT:-8080} -jar target/BeautyBook-3.2.1.jar
