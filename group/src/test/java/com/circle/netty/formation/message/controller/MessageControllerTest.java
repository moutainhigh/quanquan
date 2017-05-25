package com.circle.netty.formation.message.controller;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.hbase.CHbase;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.circle.netty.formation.GROUP;
import com.circle.netty.formation.message.model.AndroidMessage;
import com.circle.netty.formation.message.model.Question;
import com.circle.netty.formation.message.model.SysMessage;
import com.circle.netty.formation.message.model.User;
import com.circle.netty.formation.message.service.MessageService;
import com.circle.netty.formation.util.*;
import com.circle.netty.http.HttpServer;
import com.circle.netty.http.JsonParams;
import com.circle.netty.http.Urls;
import com.circle.netty.http.client.NettyClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.PropertyConfigurator;
import org.fomky.browser.core.JsonTool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Created by cxx on 15-8-11.
 */
public class MessageControllerTest{
    public static final String host = "http://172.16.1.38:8071";
//    public static final String host = "http://172.16.1.38:8081";
    private Logger logger = LoggerFactory.getLogger(MessageControllerTest.class);
    MessageService messageService;
    @Before
    public void beforeInitial() throws Exception {
        String configPath = "config/";
        //CHbase.instance(configPath+"hbase-site.xml");
        Config cfg = new Config(configPath + "app.properties");
        //CElastic.inital(cfg);
        Redis.initialShard(cfg);
        AppConfig.inital(cfg);
        logger.info("AppConfig.igexin_host====="+AppConfig.igexin_host);
        logger.info("AppConfig.igexin_appkey====="+AppConfig.igexin_appkey);
        logger.info("AppConfig.igexin_master====="+AppConfig.igexin_master);
        PropertyConfigurator.configure(configPath + "log4j.properties");
        //初始化Elastic 连接
        CElastic.inital(cfg);
        //初始化Hbase连接
        CHbase.instance(configPath+"hbase-site.xml");
        //加载敏感词,初始化到内存中
        SensitivewordFilter.init(configPath + "words.dlt");
        GROUP.executor = Executors.newCachedThreadPool();
        //初始化那个接口数据
        Urls.create("uris_formation.xml");
        AppConfig.producerPool = new ProducerPool<>(configPath+"producer.properties");
        messageService  = new MessageService();
        try {
            Redis.initial(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQuestion() throws Exception {
        final String url = "/message/question";
        Map<String,String> map = new HashMap<>();//设置参数
        map.put("qid","8c2a7afa717949d99bfffec655dd5683");
        map.put("status","9");
        map.put("deal","0");
        map.put("size","400");
        map.put("type","1");
        map.put("start","0");
        map.put("score","1");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line=reader.readLine())!=null){
            builder.append(line);
        }
        logger.info("Back From " + host + url + "  Body Context : " + JsonTool.formatJson(builder.toString(), "\t"));
    }

    @Test
    public void testAlipay() throws Exception {
        final String url = "/message/alipay";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("uid","284c6273f8eb4b4a9d3b5b1f27d73f74");
        map.put("status","1");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line=reader.readLine())!=null){
            builder.append(line);
        }
        logger.info("Back From " + host + url + "  Body Context : " + JsonTool.formatJson(builder.toString(), "\t"));
    }

    @Test
    public void testBoard() throws Exception {
        //disid=disid, Mixed:
        // context=1|5|1|1|15|0|0|0|0|100.0|80|b6072e5f-1567-4d3d-a61b-2486f6861ed1|eee07ed144c25997b4b91bcdf8021dc7|测试拉人券100|测试|99f68f1604774e0b9b3a8121dcdff758,
        // Mixed: from=5933bce082944a29aa64aea78098ec28
        final String url = "/message/addcoupon";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("uid","811bdb73b86e4d18b67de7e5dfa939ae");
        map.put("disid","disid");
        map.put("context","1|5|1|1|15|0|0|0|0|100.0|80|b6072e5f-1567-4d3d-a61b-2486f6861ed1|eee07ed144c25997b4b91bcdf8021dc7|测试拉人券100|测试|99f68f1604774e0b9b3a8121dcdff758");
        map.put("from","5933bce082944a29aa64aea78098ec28");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line=reader.readLine())!=null){
            builder.append(line);
        }
        logger.info("Back From " + host + url + "  Body Context : " + JsonTool.formatJson(builder.toString(), "\t"));
    }

    @Test
    public void testRaobque1() throws Exception {
        GROUP.executor = Executors.newFixedThreadPool(100);
        AppConfig.inital(new Config("config/app.properties"));
        System.out.println(AppConfig.igexin_appid);
        MessageService messageService = new MessageService();
        Map<String,Object> map = new HashMap<>();
        map.put("type","1");
        List<String> to = new ArrayList<>();
        to.add("ac6b37477b3220bc46424d6adda8e8d9f7c74f10ebd48cae20985d4e0e774b4b");
        to.add("9d22bc8f885bfdc96ffe5c37ac7545d9dfc2795de3679248f4c7acf8936ccc11");
        messageService.new_tuisong_app(to, "123123", map,true);
//        messageService.ios_new_tuisong("9d22bc8f885bfdc96ffe5c37ac7545d9dfc2795de3679248f4c7acf8936ccc11","测试推送",true,0,map);
        Thread.sleep(3000);
    }

    @Test
    public void testQuestion1() throws Exception {

    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testTranscid() throws Exception {
        final String url = "/message/transcid";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("type","cesjo");
        map.put("json","{\"test\":123}");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        String line = EntityUtils.toString(response.getEntity());
        logger.info("Back From " + host + url + "  Body Context : " + JsonTool.formatJson(line.toString(), "\t"));
    }
    @Test
    public void testTranscid2() throws Exception {
        final String url = "/message/transcid";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("uid","35551b4700ca403382d4f438004d1401");
        map.put("type","myque");
        map.put("json","[{\"ptid\":\"M0015\",\"price\":\"0.01\",\"iscom\":1,\"rdesc\":\"至尊vip\",\"type\":0},{\"ptid\":\"M0016\",\"price\":\"0.02\",\"iscom\":1,\"rdesc\":\"至尊vip\",\"type\":1},{\"ptid\":\"M0014\",\"price\":\"1000\",\"iscom\":1,\"rdesc\":\"801-1000元\",\"type\":1},{\"ptid\":\"M0013\",\"price\":\"500\",\"iscom\":1,\"rdesc\":\"401-500元\",\"type\":1},{\"ptid\":\"M0012\",\"price\":\"400\",\"iscom\":1,\"rdesc\":\"301-400元\",\"type\":1},{\"ptid\":\"M0011\",\"price\":\"300\",\"iscom\":1,\"rdesc\":\"201-300元\",\"type\":1},{\"ptid\":\"M0010\",\"price\":\"200\",\"iscom\":1,\"rdesc\":\"101-200元\",\"type\":1},{\"ptid\":\"M0009\",\"price\":\"100\",\"iscom\":1,\"rdesc\":\"51-100元\",\"type\":1},{\"ptid\":\"M0008\",\"price\":\"50\",\"iscom\":1,\"rdesc\":\"31-50元\",\"type\":1},{\"ptid\":\"M0007\",\"price\":\"30\",\"iscom\":1,\"rdesc\":\"26-30元\",\"type\":1},{\"ptid\":\"M0006\",\"price\":\"25\",\"iscom\":0,\"rdesc\":\"21-25元\",\"type\":1},{\"ptid\":\"M0005\",\"price\":\"20\",\"iscom\":0,\"rdesc\":\"16-20元\",\"type\":1},{\"ptid\":\"M0004\",\"price\":\"15\",\"iscom\":0,\"rdesc\":\"11-15元\",\"type\":0},{\"ptid\":\"M0003\",\"price\":\"10\",\"iscom\":0,\"rdesc\":\"6-10元\",\"type\":0},{\"ptid\":\"M0002\",\"price\":\"5\",\"iscom\":0,\"rdesc\":\"3-5元\",\"type\":0},{\"ptid\":\"M0001\",\"price\":\"1\",\"iscom\":0,\"rdesc\":\"1-2元\",\"type\":0}]");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    private void sout(CloseableHttpResponse response) throws IOException {
        String line = EntityUtils.toString(response.getEntity());
        logger.info("  Body Context : " + JsonTool.formatJson(line.toString(), "\t"));
    }

    @Test
    public void testTransapp() throws Exception {
        final String url = "/message/transapp";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
//        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
//        map.put("context","cesjo");
//        map.put("title","cesjo");
        map.put("type","cesjo");
//        map.put("sys","cesjo");
        map.put("json","{\"test\":123}");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    @Test
    public void testPushcid() throws Exception {
        GROUP.executor = Executors.newFixedThreadPool(100);
        Config conf = new Config("config/app.properties");
        AppConfig.inital(conf);
        MessageService service = new MessageService();
        User user = new User();
        Map<String,Object> hash = new HashMap<>();
        hash.put(AndroidMessage.att_cmd, AndroidMessage.que_iusse);
        hash.put(AndroidMessage.att_data, "1a3ab36d68384acf9e26036026f89268");
        user.setSystem("ios");
        user.setSystem("android");
        user.setDevice("ac6b37477b3220bc46424d6adda8e8d9f7c74f10ebd48cae20985d4e0e774b4b");
        user.setDevice("f85c700de511621d2f31951dd98bacf6e542750f7d1f50883707aad0d77f3469");
        user.setCid("97032de21a3c1622754e41e6b8faf87f");
        user.uid("1d1869f705f9498ba811164c2a9b8eae");
        service.sendTuiSong_new(user,"问啊:测试没有认证回事什么样子的",0,hash,0);
        Thread.sleep(5000);
    }

    @Test
    public void testPushapp() throws Exception {
        final String url = "/message/pushapp";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("context","cesjo");
        map.put("title",new String("就是测试推送".getBytes(),"ISO-8859-1"));
        map.put("type","cesjo");
        map.put("sys","cesjo");
        map.put("json","{\"test\":123}");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    @Test
    public void testSysmsg() throws Exception {
        final String url = "/message/sysmsg";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("context","cesjo");
        map.put("title",new String("就是测试推送".getBytes(),"ISO-8859-1"));
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    @Test
    public void testAddsysmsg() throws Exception {
        NettyClient.post("http://baidu.com", new HashMap<String, String>());
        long stat = System.currentTimeMillis();
        final String url = "/message/addsysmsg";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("uid", "35551b4700ca403382d4f438004d1401");
        SysMessage message = new SysMessage();
//        message.setAbout(SysMessage.about_coupon);
        message.setContext("111111111111111111111111");
        message.setLogo(SysMessage.SYS_LOG);
        message.setTime(System.currentTimeMillis());
        message.setQid("ksjdlakjsdlkajsdlk");
        message.setTitle("任务消息");
        message.setType(SysMessage.type_sys);
        message.setUrl("");
        map.put("json", Json.json(message));
        map.put(JsonParams.type,"0");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        String builder = EntityUtils.toString(response.getEntity());
        logger.info("times = " + (System.currentTimeMillis() - stat));
        logger.info("Back From " + host + url + "  Body Context : " + JsonTool.formatJson(builder, "\t") );
    }

    @Test
    public void testQuestion2() throws Exception {
        final String url = "/message/pushapp";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("context","cesjo");
        map.put("title",new String("就是测试推送".getBytes(),"ISO-8859-1"));
        map.put("type","cesjo");
        map.put("sys","cesjo");
        map.put("json","{\"test\":123}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    @Test
    public void testBackMoney() throws Exception {
        final String url = "/message/pushapp";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("context","cesjo");
        map.put("title",new String("就是测试推送".getBytes(),"ISO-8859-1"));
        map.put("json","{\"test\":123}");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }
    @Test
    public void testAddSysmesage() throws Exception {
        final String url = "/message/pushapp";
        Map<String,String> map = new HashMap<>();//设置参数 284c6273f8eb4b4a9d3b5b1f27d73f74
        map.put("cid","4080c65d1f1374202057fdd4b3599c79");
        map.put("context","cesjo");
        map.put("title",new String("就是测试推送".getBytes(),"ISO-8859-1"));
        map.put("type","cesjo");
        map.put("sys","cesjo");
        map.put(JsonParams.price,"{\"cash\":1000}");
        CloseableHttpResponse response = NettyClient.post(host + url, map);
        sout(response);
    }

    @Test
    public void testRankChange(){
        MessageService service = new MessageService();
        Question question = new Question();
        question.setAuid("0000d48c02de454f8a3c648835b2866f");
        question.setQuid("00011c6977df4713b23dd3cf7adba161");
        long start = System.currentTimeMillis();
        for (int i=0;i < 1000;i++){
            //service.rankChange(question,"20160330",14);
            findRankInfo();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时 == " + (end - start));

    }


    /**
     *
     String auid = "066edb135e284884b2c96cd676e01950";
     String quid = "00011c6977df4713b23dd3cf7adba161";
     String auid = "0dcf3f70e83640238bd43608f452dce7";
     String quid = "0f078c3d1d8a49eb8299969625f328b2";
     String auid = "110a00082d404ee2979b1b8a396219d4";
     String quid = "1113458588094e49b45805223b3fadaa";
     */
    @Test
    public void findRankInfo(){
        //3792528de9534d8f99f27a33009f69e1   11
        //1784c58389894848bab0377dae7a1232   10
        //9662de57eeb34693b1bb5ff16e49f5a7 016
        //3c4d9f1dbdca457e8c0895466651ce4f 17114
        //6abe1b07285a4ef0a9e0fa4fb2d2caee 021
        //3314b263862f4697af9e152cd4e21ddf 19022
        String userId = "e871ec883a3f40b398ef67e2cf95261d";
        String totalAnsTimes = Redis.shard.get(RedisTable.RANGE_ANSQUES_TIMES + userId); //总回答次数
        String totalQueTimes = Redis.shard.get(RedisTable.RANGE_QUES_TIMES + userId); //总提问次数

        long nowLong = System.currentTimeMillis();
        String yyyyMMdd = TimeUtil.formatLongToStr(nowLong, "yyyyMMdd");
        String todayAnsTimesKey = RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+userId;
        String todayAskTimesKey = RedisTable.RANGE_QUES_DAY_TIMES + yyyyMMdd + "|"+userId;
        String todayAnsTimes = Redis.shard.get(todayAnsTimesKey);//今天回答次数
        todayAnsTimes = todayAnsTimes==null?"0":todayAnsTimes;
        String todayAskTimes = Redis.shard.get(todayAskTimesKey);//今天提问次数
        todayAskTimes = todayAskTimes==null?"0":todayAskTimes;

        String lastAnsTimeLong = Redis.shard.get(RedisTable.LAST_ANS_TIME + userId);//最后的答题时间
        String lastQueTimeLong = Redis.shard.get(RedisTable.LAST_QUE_TIME + userId);//最后的提问时间

        int week = TimeUtil.weekOfYear(nowLong);
        String thisWeekAnsKey = RedisTable.RANGE_ANSQUES_DAY_TIMES_WEEK + week + "|"+userId;//每周回答
        String thisWeekQusKey = RedisTable.RANGE_QUES_DAY_TIMES_WEEK + week + "|"+userId; // 每周提问
        String thisWeekAnsTimes = Redis.shard.get(thisWeekAnsKey);//用户本周回答次数
        String thisWeekQueTimes = Redis.shard.get(thisWeekQusKey);//用户本周的提问次数

        String thisWeekLastAnsTimeLong = Redis.shard.get(RedisTable.LAST_ANS_TIME_WEEK + userId);//本周最后的答题时间
        String thisWeekLastQueTimeLong = Redis.shard.get(RedisTable.LAST_QUE_TIME_WEEK + userId);//本周最后的提问时间

        String todayAppTimes = Redis.shard.get(RedisTable.RANGE_MARK_TIMES + yyyyMMdd + "|" + userId);//今天被评价次数
        String todayAppScore = Redis.shard.get(RedisTable.RANGE_MARK_SCORE + yyyyMMdd + "|" + userId);// 今天被评价分数
        String todayLastAppTimeLong = Redis.shard.get(RedisTable.LAST_COMMOT_TIME + yyyyMMdd + "|" + userId);//最后一次被评价的时间

        String thisWeekAppTimes = Redis.shard.get(RedisTable.RANGE_MARK_TIMES_WEEK + week + "|" + userId);//本周被评价次数
        String thisWeekyAppScore = Redis.shard.get(RedisTable.RANGE_MARK_SCORE_WEEK + week + "|" + userId);// 本周被评价分数
        String thieWeekLastAppTimeLong = Redis.shard.get(RedisTable.LAST_COMMOT_TIME_WEEK + week + "|" + userId);//本周最后一次被评价的时间

        String todayAppOtherTimes = Redis.shard.get(RedisTable.RANGE_MARK_OHTER_TIMES + yyyyMMdd + "|" + userId);//评价次数
        String todayAppOtherScore = Redis.shard.get(RedisTable.RANGE_MARK_OTHER_SCORE + yyyyMMdd + "|" + userId);// 今天评价分数
        String todayLastAppOtherTimeLong = Redis.shard.get(RedisTable.LAST_COMMOT_OTHTER_TIME + yyyyMMdd + "|" + userId);//最后一次评价的时间

        String thisWeekAppOtherTimes = Redis.shard.get(RedisTable.RANGE_MARK_OHTER_TIMES_WEEK + week + "|" + userId);//本周评价次数
        String thisWeekAppOtherScore = Redis.shard.get(RedisTable.RANGE_MARK_OTHER_SCORE_WEEK + week + "|" + userId);// 本周今天被评价分数
        String thisWeekLastAppOtherTimeLong = Redis.shard.get(RedisTable.LAST_COMMOT_OTHTER_TIME_WEEK + week + "|" + userId);//本周最后一次评价的时间

        Long zrevrankTutor = Redis.shard.zrevrank(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, userId); //用户在最强导师排行
        Double zrevrankTutorScore = Redis.shard.zscore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, userId); //用户在最强导师中的分数
        Long zrevrankSuperscholar = Redis.shard.zrevrank(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar, userId); //用户在超级学霸排行 TODAYRANK|20160408|SUPER
        Double zrevrankSuperscholarScore = Redis.shard.zscore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar, userId); //用户在超级学霸排行分数
        Long zrevrankMaze = Redis.shard.zrevrank(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, userId); //用户在究极妹子排行
        Double zrevrankMazeScore = Redis.shard.zscore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, userId); //用户在究极妹子排行分数
        //ZREVRANGEBYSCORE  page_rank  10 0 limit  1 1
        //在最强导师中，比他上一名的用户
        int tutorThenTimes = 0; // 在最强导师中，他的上一名比他多回答的次数
        if(zrevrankTutorScore!=null && zrevrankTutorScore >0){
            Set<String> stringsTutor = Redis.shard.zrevrangeByScore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.tutor, zrevrankTutorScore +1, 0, 0, 1);
            for(String str:stringsTutor){
                if(str!=null){
                    String todayBeforeAnsTimes = Redis.shard.get(RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+str);//今天回答次数

                    tutorThenTimes = Integer.parseInt(todayBeforeAnsTimes==null?"0":todayBeforeAnsTimes) - Integer.parseInt(todayAnsTimes);
                }
            }
        }

        int SuperThenTimes = 0; //学霸中，他比上一名少答的次数
        if(zrevrankSuperscholarScore!=null && zrevrankSuperscholarScore >0){
            Set<String> stringsSuper = Redis.shard.zrevrangeByScore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.superscholar, zrevrankSuperscholarScore+1, 0, 0, 1);
            for(String str : stringsSuper){
                if(str != null){
                    String todayBeforeAnsTimes = Redis.shard.get(RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+str);//今天回答次数  如果他的前一位是空，说明他是第一或者库里数据为空
                    SuperThenTimes = Integer.parseInt(todayBeforeAnsTimes==null?"0":todayBeforeAnsTimes) - Integer.parseInt(todayAnsTimes);
                }
            }
        }

        int mazeThenTimes = 0; //妹子中，他比上一名少答的次数
        if(zrevrankMazeScore != null && zrevrankMazeScore > 0){
            Set<String> stringsMaze = Redis.shard.zrevrangeByScore(RedisTable.TODAYRANKING + yyyyMMdd + "|" + RedisTable.maze, zrevrankMazeScore+1, 0,0, 1);
            for(String str: stringsMaze){
                if(str!=null){
                    String todayBeforeAnsTimes = Redis.shard.get(RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+str);//今天回答次数
                    mazeThenTimes = Integer.parseInt(todayBeforeAnsTimes==null?"0":todayBeforeAnsTimes) - Integer.parseInt(todayAnsTimes);
                }
            }
        }


        Long zrevrankTutorWeek = Redis.shard.zrevrank(RedisTable.WEEKRANKING + week + "|" + RedisTable.tutor, userId); //用户在最强导师排行(每周)
        Long zrevrankSuperscholarWeek = Redis.shard.zrevrank(RedisTable.WEEKRANKING + week + "|" + RedisTable.superscholar, userId); //用户在超级学霸排行(每周)
        Long zrevrankMazeWeek = Redis.shard.zrevrank(RedisTable.WEEKRANKING + week + "|" + RedisTable.maze, userId); //用户在究极妹子排行(每周)


        logger.info("总回答次数="+totalAnsTimes);
        logger.info("总提问次数="+totalQueTimes);
        logger.info("今天回答次数="+todayAnsTimes);
        logger.info("今天提问次数="+todayAskTimes);
        logger.info("最后的答题时间="+lastAnsTimeLong);
        logger.info("最后的提问时间="+lastQueTimeLong);
        logger.info("用户本周回答次数="+thisWeekAnsTimes);
        logger.info("用户本周的提问次数="+thisWeekQueTimes);
        logger.info("本周最后的答题时间="+thisWeekLastAnsTimeLong);
        logger.info("本周最后的提问时间="+thisWeekLastQueTimeLong);

        logger.info("今天回答被评价次数="+todayAppTimes);
        logger.info("今天回答被评价分数="+todayAppScore);
        logger.info("最后一次回答被评价的时间="+todayLastAppTimeLong);
        logger.info("本周回答被评价次数="+thisWeekAppTimes);
        logger.info("本周回答被评价分数="+thisWeekyAppScore);
        logger.info("本周回答最后一次被评价的时间="+thieWeekLastAppTimeLong);

        logger.info("今天提问被评价次数="+todayAppOtherTimes);
        logger.info("今天提问被评价分数="+todayAppOtherScore);
        logger.info("今天提问最后一次被评价的时间="+todayLastAppOtherTimeLong);
        logger.info("本周提问评价次数="+thisWeekAppOtherTimes);
        logger.info("本周提问被评价分数="+thisWeekAppOtherScore);
        logger.info("本周最后提问一次评价的时间="+thisWeekLastAppOtherTimeLong);

        logger.info("用户在最强导师排行="+zrevrankTutor);
        logger.info("用户在最强学霸排行="+zrevrankSuperscholar);
        logger.info("用户在究极妹子排行="+zrevrankMaze);

//        logger.info("用户在每周最强导师排行="+zrevrankTutorWeek);
//        logger.info("用户在每周最强学霸排行="+zrevrankSuperscholarWeek);
//        logger.info("用户在每周究极妹子排行="+zrevrankMazeWeek);

        logger.info("用户在最强导师排行 比上一名少答题目数="+tutorThenTimes);
        logger.info("用户在最强学霸排行 比上一名少答题目数="+SuperThenTimes);
        logger.info("用户在究极妹子排行 比上一名少答题目数="+mazeThenTimes);


    }



    /**
     * 插入排行榜假数据
     String auid = "066edb135e284884b2c96cd676e01950";
     String quid = "00011c6977df4713b23dd3cf7adba161";
     String auid = "0dcf3f70e83640238bd43608f452dce7";
     String quid = "0f078c3d1d8a49eb8299969625f328b2";
     String auid = "110a00082d404ee2979b1b8a396219d4";
     String quid = "1113458588094e49b45805223b3fadaa";
     */
    @Test
    public void initRankDate(){
        String auid = "110a00082d404ee2979b1b8a396219d4";
        String quid = "1113458588094e49b45805223b3fadaa";
        Redis.shard.set(RedisTable.RANGE_ANSQUES_TIMES+auid,"23");//答题者总回答次数加1
        Redis.shard.set(RedisTable.RANGE_QUES_TIMES+quid,"55");  //提问者总提问次数加1
        long nowLong = System.currentTimeMillis();
        String yyyyMMdd = TimeUtil.formatLongToStr(nowLong, "yyyyMMdd");
        String todayAnsTimesKey = RedisTable.RANGE_ANSQUES_DAY_TIMES + yyyyMMdd + "|"+auid;
        String todayAskTimesKey = RedisTable.RANGE_QUES_DAY_TIMES + yyyyMMdd + "|"+quid;

        Redis.shard.incrBy(todayAnsTimesKey,11);//用户今天回答次数+1
        Redis.shard.expire(todayAnsTimesKey,24*3600); //设置用户今天答题计数器过期时间为1天
        Redis.shard.incrBy(todayAskTimesKey,22);//用户今天的提问次数 +1
        Redis.shard.expire(todayAskTimesKey,24*3600);

        Redis.shard.set(RedisTable.LAST_ANS_TIME +auid, String.valueOf(nowLong)); //答题者最后的答题时间
        Redis.shard.set(RedisTable.LAST_QUE_TIME +quid, String.valueOf(nowLong)); //提问者最后的提问时间


        //计算周排行榜数据
        int week = TimeUtil.weekOfYear(nowLong);
        logger.info("week -======" + week);
        String thisWeekAnsKey = RedisTable.RANGE_ANSQUES_DAY_TIMES_WEEK + week + "|"+auid;//每周回答
        String thisWeekQusKey = RedisTable.RANGE_QUES_DAY_TIMES_WEEK + week + "|"+quid; // 每周提问
        Redis.shard.incrBy(thisWeekAnsKey, 44);//用户本周回答次数+1
        Redis.shard.expire(thisWeekAnsKey,7*24*3600); //设置用户本周答题计数器过期时间为1周
        Redis.shard.incrBy(thisWeekQusKey,66);//用户本周的提问次数 +1
        Redis.shard.expire(thisWeekQusKey,7*24*3600);//用户本周提问次数+1

        Redis.shard.set(RedisTable.LAST_ANS_TIME_WEEK + auid, String.valueOf(nowLong)); //本周答题者最后的答题时间
        Redis.shard.set(RedisTable.LAST_QUE_TIME_WEEK + quid, String.valueOf(nowLong)); //本周提问者最后的提问时间


        //计算评价之后的分数变动
        Redis.shard.incrBy(RedisTable.RANGE_MARK_OHTER_TIMES + yyyyMMdd + "|" + quid,22); //评价次数 +1
        Redis.shard.incrBy(RedisTable.RANGE_MARK_OTHER_SCORE + yyyyMMdd + "|" + quid, (long)(66)); // 今天被评价分数
        Redis.shard.set(RedisTable.LAST_COMMOT_OTHTER_TIME + yyyyMMdd + "|" + quid, String.valueOf(nowLong) ); //最后一次评价的时间

        //记录周排行榜数据
        Redis.shard.incrBy(RedisTable.RANGE_MARK_OHTER_TIMES_WEEK + week + "|" + quid,33); //本周评价次数 +1
        Redis.shard.incrBy(RedisTable.RANGE_MARK_OTHER_SCORE_WEEK + week + "|" + quid, (long)(33)); // 本周今天被评价分数
        Redis.shard.set(RedisTable.LAST_COMMOT_OTHTER_TIME_WEEK + week + "|" + quid, String.valueOf(nowLong) ); //本周最后一次评价的时间

        //计算评价之后分数的变动
        Redis.shard.incrBy(RedisTable.RANGE_MARK_TIMES + yyyyMMdd + "|" + auid,54); //被评价次数 +1
        Redis.shard.incrBy(RedisTable.RANGE_MARK_SCORE + yyyyMMdd + "|" + auid, (long)(55+11+22)); // 今天被评价分数
        Redis.shard.set(RedisTable.LAST_COMMOT_TIME + yyyyMMdd + "|" + auid, String.valueOf(nowLong) ); //最后一次被评价的时间
        //发送请求到group，重新计算排行榜数据

        //记录周排行榜数据
        Redis.shard.incrBy(RedisTable.RANGE_MARK_TIMES_WEEK + week + "|" + auid,55); //本周被评价次数 +1
        Redis.shard.incrBy(RedisTable.RANGE_MARK_SCORE_WEEK + week + "|" + auid, (long)(20+11+26)); // 本周被评价分数
        Redis.shard.set(RedisTable.LAST_COMMOT_TIME_WEEK + week + "|" + auid, String.valueOf(nowLong) ); //本周最后一次被评价的时间
    }



    @Test
    public void sendMsgQuesPublish_ThreadTest() throws IOException {
        Question que = messageService.findQuestionByQid("3c784d55534c4695a445da17fc52a350");
        //34 116
        //messageService.sendPub(que,34,0,0,null,100);
        messageService.sendMsgQuesPublish_Thread(que,800);
    }


}