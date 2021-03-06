#!/usr/bin/env bash

nohup java -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSClassUnloadingEnabled \
-XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -Xloggc:garbage-collection.log -XX:+HeapDumpOnOutOfMemoryError \
-XX:+UseBiasedLocking -XX:+UseFastAccessorMethods -Djava.util.concurrent.ForkJoinPool.common.parallelism=16 -d64 -server -Xms512m -Xmx512m -Xmn256m \
-XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -jar ./c-delay-queue-0.0.1-SNAPSHOT.jar &
