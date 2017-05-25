#!/bin/bash
#服务主目录
server_version=0.1.2
server_home=~/task-${server_version}
server_home_sh="${server_home}/auto_appr_question.sh"
# -XX:+UseG1GC
JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
server_config_path=${server_home}/config/
server_class_path=${server_home}/task_que_list.jar
libs=$(ls ${server_home}/lib)
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
#server_class_path="${server_class_path}:${server_home}/lib/*.jar"
#=================================================================
# 参数1 : 配置文件目录
# 参数2 : 多少小时前的死题(默认25小时)
# 参数3 : 每次处理题目数量 (默认100)
# 注意配置 group 的lvs 配置 , 取消订单是通过 调用group 的自动评价订单接口 实现
#   group.lvs.server=lvs-group.wenaaa.com:8081 TaskAutoApprQuestion
#=================================================================
server_class_path="${server_config_path}:${server_class_path}"
cd ${server_home}
if [ $# -eq 2 ];then
    java -cp ${server_class_path} com.circle.wena.task.dead.TaskAutoApprQuestion $1 $2
elif [ $# -eq 3 ] ;then
    java -cp ${server_class_path} com.circle.wena.task.dead.TaskAutoApprQuestion $1 $2 $3
elif [ $# -eq 1 ] ;then
    java -cp ${server_class_path} com.circle.wena.task.dead.TaskAutoApprQuestion $1
fi