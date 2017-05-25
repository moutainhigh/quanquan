

## 2016年1月15日17:20:17 更新
1. Fix Bug 
    1) 新题目中推送,过滤铭感词
    2) 
    

## 2016年1月4日18:01:53 更新
1. 修复BUG
    * 个推推送列表上限设置(个推上限1000-默认设置999)
2. 新加接口
    * 拉人发现金了
        
        
        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        表设计 : 
        CREATE TABLE IF NOT EXISTS CIRCLE.LCASH (
          id      VARCHAR(40) NOT NULL PRIMARY KEY, -- qid, 流水ID
          cash    DECIMAL,      --充值 添加金钱
          cdate   BIGINT,       --充值时间
          status  INTEGER,      -- 状态 0 领取(暂时没有其他状态)
          uid     VARCHAR(40),  -- 领取人ID
          fuid    VARCHAR(40),  -- 通过谁触发 领取
          GYEAR   VARCHAR(64),  --精确到年
          GMONTH  VARCHAR(64),  --精确到月
          GDAY    VARCHAR(64),  --精确到日
          GHOUR   VARCHAR(64),  --精确到小时
          GMINUTE VARCHAR(64),  --精确到分钟
          GSEC    VARCHAR(64)   --精确到秒
        );
        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        URL : /action/addmoney
        参数(request parameters) : 
            uid :(String) 被加钱钱的人(即接收现金的用户的ID)
            cash :(String) 添加金额
            from :(String) 事件触发者,即此用户完成拉人条件后出发的给uid用户加钱钱
            msg :(String) 推送以及流水详情显示内容
            type :(String) 是否发送系统消息 , 0 发送(默认); 1 不发送
            code :(String) 是否推送 , 0 推送(默认) ; 1 不推送
            cmd :(String) 命令 - > 跳转命令 待定 默认0 不跳转
        接口调用返回(response) : 
               sta :(int) 0(失败)/1(成功)
               err :(String) 错误信息
        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

## 2015年11月18日14:07:29 更新
1. 自检接口
    * URL : /action/check
    * 请求类型: post
    * 返回结果 : 正常 返回 200 状态
                不正常 返回 500 状态<br>
2. 删除用户优惠券
    * URL : /action/coupon
    * 请求类型: post
    * 返回数据类型 :  json 
    * 参数说明;
    *  disid : 及优惠券Id
    *  uid : 用户Id
    *  mobile : 用户手机号
    *      注意 : mobile 和 uid 只需上传其中一个即可
3. 修改推送文字
    * 新问题推送提醒,讲内容中的"问啊"->【问题类型名称】
    
## 2015年09月16日17:04:46 更新
1. 抢答更改 , 提前2秒(根据平均延迟时间设定) , 进入抢答阶段后3秒抢答的用户.放入set中,排队等待系统通知
2. 添加苹果推送 .... 未完成 

## 2015年09月07日12:45:19 更新
版本：Ｖ-0.1.3
### $ 性能优化 - 提升指数 - (未测)
优化反射 链接直接映射 Method 实例.无需每次都获取class.getMethod(....)

## 2015年09月05日16:00:55 更新 
添加接口 - 给用户添加系统消息
/message/addsysmsg
参数列表
    uid : 用户ID
    cid : 用户个推ID
    json : SysMessage.create()

## 1 接口调用说明
### 1.1 对单用户发送透传消息
URL : /message/transcid
参数
    type : String ; 类型
    json : String ; json 字符串,前端要接收数据
    cid  : String ; 接受人Cid
### 1.2 对整个APP发送透传消息
UTL : /message/transapp
参数
    type : String ; 类型
    json : String ; json 字符串,前端要接收数据
    tag  : String ; 标签,用户标签可以不用填写
    phone : String ; 手机型号 , 可不用填写
    province : String ; 省份 , 可不用填写
        
### 1.3 对单个人发送推送
URL : /message/pushcid
    参数
    sys  : String ; 系统 ios / android ,如果不是ios 默认为android
    cid  : String ; 接受人Cid
    title  : String ; 
    context  : String ;
    type : String ; 类型
    json : String ; json 字符串,前端要接收数据
### 1.4 对整个APP的用户发发送推送消息
URL : /message/pushapp
    参数
    sys  : String ; 系统 ios / android ,如果不是ios 默认为android
    title  : String ; 标题
    context  : String ; 推送内容
    type : String ; 类型, 为自定义消息类型
    json : String ; json 字符串,前端要接收数据, 为自定义消息数据
    phone : String ; 手机型号 , 可不用填写
    province : String ; 省份 , 可不用填写
### 1.5 给用户添加优惠券
URL : /message/addcoupon
    参数
    uid :  用户ID
    disid : 优惠券ID
    context :  优惠券数据信息, 详细请看 com.circle.netty.formation.message.model.Coupon 类
        
##启动说明
配置
close.sh
host: 本机IP/或者host
port : 本服务的端口号

formation.properties
###端口
server.port=8081
###客户端request 请求内容大小
server.reques.context.length=1024000
###本机IP
server.ip=10.2.138.13
###pid 文件存放目录
server.pid.path=/data/pid/FormationServer

config/jssecacerts
环信公钥,需要就讲其放入到," ${JAVA_HOME}/jre/lib/security/ " 文件夹下
        