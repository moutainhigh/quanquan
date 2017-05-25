#!/bin/bash
server_home=/home/hadoop/test/sendmaster2
server_home_sh="${server_home}/bin/run.sh"
JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
# 参数1 , 设置配置文件路径,
server_config_path=${server_home}/
libs=$(ls ${server_home}/ruleFilter_lib)
server_class_path=${server_home}/ruleFilter.jar
#server_class_path="${server_config_path}:${server_class_path}"
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/ruleFilter_lib/${jars}"
done
cd ${server_config_path}
java $JAVA_OPT -classpath ${server_class_path} com.rulesfilter.yy.zj.main.RuleFilter $1 &
#==结束=============================================================================


