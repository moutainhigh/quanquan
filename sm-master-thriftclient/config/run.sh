#!/usr/bin/env bash
#JAVA_HOME=/usr/java/jdk1.7.0_80
#程序目录
server_home=/home/hadoop/test/sendmaster2 修改为程序目录
server_home_sh="${server_home}/bin/run.sh"
JAVA_OPT="-server -Xms256m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
# 参数1 , 设置配置文件路径,
server_config_path=${server_home}/
libs=$(ls ${server_home}/lib)
server_class_path=${server_home}/time_thriftclient.jar
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
cd ${server_config_path}
java $JAVA_OPT -classpath ${server_class_path} com.lexjishu.thrift.yy.AppExe $1 &



