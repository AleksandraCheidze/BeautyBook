#!/bin/bash

echo "Testing BeautyBook memory optimization locally..."

# Build the application first
echo "Building application..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed. Please fix compilation errors first."
    exit 1
fi

# Test with optimized settings
echo "Starting application with optimized memory settings..."

# Same settings as Railway Procfile
OPTIMIZED_OPTS="-Xms256m -Xmx512m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseCompressedOops -XX:+UseCompressedClassPointers"

# Add GC logging for monitoring
GC_LOGGING="-Xlog:gc*:logs/gc-optimized.log:time,tags"

# Application settings
APP_OPTS="-Dspring.profiles.active=local -Dserver.port=8081"

echo "JVM Options: ${OPTIMIZED_OPTS} ${GC_LOGGING} ${APP_OPTS}"
echo
echo "Starting application... (Press Ctrl+C to stop)"
echo "Monitor memory usage with: ps -o pid,vsz,rss,comm -p \$!"
echo

# Create logs directory
mkdir -p logs

# Start the application
java ${OPTIMIZED_OPTS} ${GC_LOGGING} ${APP_OPTS} -jar target/BeautyBook-3.2.1.jar
