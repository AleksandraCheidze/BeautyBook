@echo off
echo Starting BeautyBook with optimized JVM settings...

REM JVM Memory Settings
set HEAP_MIN=512m
set HEAP_MAX=1g
set METASPACE_MAX=256m

REM GC Settings (G1GC optimized for low latency)
set GC_OPTS=-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1MixedGCCountTarget=8 -XX:InitiatingHeapOccupancyPercent=45

REM JIT Compiler optimizations
set JIT_OPTS=-XX:+TieredCompilation -XX:TieredStopAtLevel=4 -XX:+UseStringDeduplication

REM Memory optimizations
set MEM_OPTS=-XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:CompressedClassSpaceSize=128m

REM JMX Monitoring
set JMX_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false

REM GC Logging (for monitoring)
set GC_LOG_OPTS=-Xlog:gc*:logs/gc.log:time,tags -Xlog:safepoint:logs/safepoint.log:time,tags

REM Performance monitoring
set PERF_OPTS=-XX:+FlightRecorder -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints

REM Application-specific optimizations
set APP_OPTS=-Dspring.jmx.enabled=true -Dspring.application.admin.enabled=true -Djava.awt.headless=true -Dfile.encoding=UTF-8

REM Combine all options
set JVM_OPTS=-Xms%HEAP_MIN% -Xmx%HEAP_MAX% -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=%METASPACE_MAX% %GC_OPTS% %JIT_OPTS% %MEM_OPTS% %JMX_OPTS% %GC_LOG_OPTS% %PERF_OPTS% %APP_OPTS%

echo JVM Options: %JVM_OPTS%
echo.

REM Create logs directory if it doesn't exist
if not exist "logs" mkdir logs

REM Start the application
java %JVM_OPTS% -jar target/BeautyBook-3.2.1.jar

pause
