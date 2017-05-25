#!/bin/bash
#服务主目录
server_version=0.1.2
#server_home=/root/SmsServer-${server_version}_test
server_home=~/task-${server_version}
server_home_sh="${server_home}/run.sh"
# -XX:+UseG1GC
JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
#启动应用 , 如果没有参数将使用默认配置
# java ${JAVA_OPT} -jar ./ArtifactServer $1>> /data/log4j/netty/logs/server.out &
server_config_path=${server_home}/config/
server_class_path=${server_home}/task_ruzhang.jar
libs=$(ls ${server_home}/lib)
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
#server_class_path="${server_class_path}:${server_home}/lib/*.jar"
#====================================================
# 参数1 : 配置文件目录
# 参数2 : 开始时间
# 参数3 : 结束时间按
#====================================================
server_class_path="${server_config_path}:${server_class_path}"
cd ${server_home}
if [ $# -eq 3 ];then
    java -cp ${server_class_path} com.circle.task.ruzhang.Main $1 $2 $3
elif [ $# -eq 1 ] ;then
    java -cp ${server_class_path} com.circle.task.ruzhang.Main $1
elif [ $# -eq 2 ] ;then
    java -cp ${server_class_path} com.circle.task.ruzhang.Main $1 $2
fi