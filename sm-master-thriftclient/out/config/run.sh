#!/bin/bash
JAVA_HOME=/application/jdk
server_home=/application/thrift-client
server_home_sh="${server_home}/bin/run.sh"
JAVA_OPT="-server -Xms256m -Xmx256m -Xmn76m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:/data/sq-thrift-server/logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
libs=$(ls ${server_home}/lib)
server_class_path=${server_home}/time_thriftclient.jar
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
cd ${server_home}
java $JAVA_OPT -classpath ${server_class_path} com.sm.master.server.yy.exec.AppExe "$@" &

