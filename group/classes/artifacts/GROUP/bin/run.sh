#!/usr/bin/env bash
#启动应用 , 如果没有参数将使用默认配置
JAVA_HOME=/usr/java/jdk1.7.0_80
server_version=0.1.3
#server_home=/root/FormationServer-${server_version}
server_home=~/GROUP-${server_version}
server_home_sh="${server_home}/bin/formation-server.sh"
#运行主类
main_class=com.circle.netty.formation.GROUP
# -XX:+UseG1GC
JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
# 参数1 , 设置配置文件路径,
server_config_path=${server_home}/config/
if [ $# -gt 0 ];then
    server_config_path=${server_home}/$1/
fi
#默认文件路径
def_config_properties="${server_config_path}formation.properties"
if [ $# -gt 1 ];then
    #读取最后一个
    def_config_properties=${server_config_path}$2
    #def_config_properties=$2
fi
server_class_path="${JAVA_HOME}/jre/bin:${server_home}/GROUP.jar"
libs=$(ls ${server_home}/lib)
for jars in ${libs};do
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
echo ${server_class_path}
cd ${server_home}
#java ${JAVA_OPT} -Djava.ext.dirs=${server_home}/lib -cp ${server_class_path} ${main_class} ${server_config_path}
java ${JAVA_OPT} -cp ${server_class_path} ${main_class} ${server_config_path}
