#JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
#java  $JAVA_OPT -jar sendmaster.jar $1 >> logs/log.out &



#启动脚本
#==开始=============================================================================
#!/usr/bin/env bash
#启动应用 , 如果没有参数将使用默认配置
# java ${JAVA_OPT} -jar ./ArtifactServer $1>> /data/log4j/netty/logs/server.out &
#JAVA_HOME=/usr/java/jdk1.7.0_80
#程序目录
server_home=/home/hadoop/test/sendmaster2
server_home_sh="${server_home}/bin/run.sh"
JAVA_OPT="-server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -Xmn384m -XX:NewRatio=1 -XX:SurvivorRatio=8 -XX:OnOutOfMemoryError=${server_home_sh} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSFullGCsBeforeCompaction=1 -XX:+UseCMSCompactAtFullCollection  -XX:ParallelCMSThreads=20 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:logs/gc.log -XX:+PrintHeapAtGC -XX:+UnlockExperimentalVMOptions -XX:MaxTenuringThreshold=7 -XX:GCTimeRatio=19 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -Dio.netty.leakDetectionLevel=paranoid"
# 参数1 , 设置配置文件路径,
server_config_path=${server_home}/
libs=$(ls ${server_home}/lib)
server_class_path=${server_home}/sendMaster.jar
#server_class_path="${server_config_path}:${server_class_path}"
for jars in ${libs};do
    echo '111111'
    server_class_path="${server_class_path}:${server_home}/lib/${jars}"
done
#server_class_path="${server_class_path}:${server_home}/lib/*.jar"
#server_class_path="${server_config_path}:${server_class_path}"
cd ${server_config_path}
#echo ${server_class_path}
java $JAVA_OPT -classpath ${server_class_path} netty.server.main.FilterMain $1 &

#==结束=============================================================================


