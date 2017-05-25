#!/usr/bin/env bash
#配置PID 路径
server_pid_path=/data/pid/FormationServer
pids=$(cat ${server_pid_path})
echo server pid is ${pids}
#发送关闭命令---
echo 'you should wait server close......'
kill -15 ${pids}


