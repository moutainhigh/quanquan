package com.sendtask.usergroup.zhoujia.kafka;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.circle.core.elastic.CElastic;
import com.circle.core.elastic.Json;
import com.circle.core.redis.Redis;
import com.circle.core.util.Config;
import com.sendtask.usergroup.zhoujia.model.Group;
import com.sendtask.usergroup.zhoujia.model.Sign;
import com.sendtask.usergroup.zhoujia.model.User;
import com.sendtask.usergroup.zhoujia.utils.StaticParam;


/**
 * @author Created by cxx on 15-7-28.
 */
public class UserGroupConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(UserGroupConsumer.class);
    private KafkaStream<byte[], byte[]> stream;
    private int threadNumber;
    //MessageStart messageStart;
    private NewUserstart newUserStart;
    private CElastic elastic;

    public UserGroupConsumer(KafkaStream<byte[], byte[]> stream, int threadNumber, NewUserstart newUserstart) {
        this.stream = stream;
        this.threadNumber = threadNumber;
        this.newUserStart = newUserstart;
        
        try {
			CElastic.inital(new Config(StaticParam.config_path+"cricle-core.properties"));
		} catch (IOException e) {
			logger.error("加载配置文件出错：cricle-core.properties"  ,e); 
		}
        elastic = CElastic.elastic();
        
    }
    
    @Override
    public void run() {
        try {
			ConsumerIterator<byte[], byte[]> it = stream.iterator();
			logger.info("Kafka UserCome Consumer Thread " + threadNumber + ": ");
			while (it.hasNext()) {
			    //======在此处理业务逻辑========================================//
			    MessageAndMetadata<byte[], byte[]> data = it.next();
			    logger.info("kafka 收到信息，key = = " + new String(data.key()));
			    GetResponse userResponse = elastic.get(User.elc_table_user, new String(data.key()));
			    String userJson = userResponse.getSourceAsString();
			    User user = (User)Json.jsonParser(userJson, User.class);
			    user.setUid(new String(data.key()));
			    logger.info("取到的用户信息 = " + userJson);
			    Group group = newUserStart.getGroup();
			    logger.info("josnInfo = = =" + Json.json(group));
			    Sign sign = new Sign();
			    isThisGroup(user, group,sign);
			    logger.info("是否属于这个组 = " + sign.isRight );
                GetResponse questionResponse = CElastic.elastic().get(User.elc_table_user, user.getUid());
                long version = questionResponse.getVersion();//更新之前先去读取一下当前的version
			    if (sign.isRight) {//用户是属于这个分组
                    //更新分组id
                    while(true){
                        try {
                            //先去读取分组id，更新，如果版本不对，再去取，重新计算，再更，直到更新成功break跳出
                            GetResponse questionResponseNew = CElastic.elastic().get(User.elc_table_user, user.getUid());
                            String groupId = String.valueOf(questionResponseNew.getSource().get("groupId"));
                            String newGroupIds = this.belongTo(groupId, String.valueOf(group.getGroupId()));
                            user.setGroupId(newGroupIds);
                            updateGroupId(user,version++);
                            break;
                        }catch (VersionConflictEngineException e){
                            logger.error("版本错误=",e);
                        }
                    }

				}else{//如果用户不属于这个组，且分组id中有这个id，则删除，比如本组是A，而用户的分组为A|B|C，判断A进程不属于该用户，则把A删除，分组改为B|C
                    //更新分组id
                    while(true){
                        try {
                            //先去读取分组id，更新，如果版本不对，再去取，重新计算，再更，直到更新成功break跳出
                            GetResponse questionResponseNew = CElastic.elastic().get(User.elc_table_user, user.getUid());
                            String groupId = String.valueOf(questionResponseNew.getSource().get("groupId"));
                            String newGroupIds = this.noteBelongTo(groupId, String.valueOf(group.getGroupId()));
                            user.setGroupId(newGroupIds);
                            updateGroupId(user,version++);
                            break;
                        }catch (VersionConflictEngineException e){
                            logger.error("版本错误=",e);
                        }
                    }
                }
			    logger.info("---------------------------分组处理完毕 ------------------------------");
			    //==============================================//
			}
			logger.info("Shutting down Thread: " + threadNumber);
			//messageStart.smss.remove(threadNumber);//移除map中数据
		} catch (Exception e) {
			logger.error("分组线程异常：",e);
		}
    }

    private String noteBelongTo(String userGroupIds,String groupId){
        //String groupId = user.getGroupId();
        String newGroupId = "";
        if(userGroupIds!=null && !userGroupIds.isEmpty() && groupId!=null){
            if(groupId.equals(userGroupIds)){
                logger.info("---------------相等的情况-------------");
                newGroupId = "-";
            }else if(userGroupIds.contains(groupId)){
                String[] split = userGroupIds.split("\\|");
                for(String str : split){
                    logger.info("iiiii=  "+str + "    是否相等="+str.equals(groupId));
                    if(!str.isEmpty()&&!str.equals(groupId)){
                        newGroupId+= (str+"|");
                    }
                }
                if(!newGroupId.isEmpty() && newGroupId.endsWith("|")){
                    newGroupId = newGroupId.substring(0,newGroupId.length()-1);
                }
                logger.info("newGroupId =" + newGroupId);
            }
        }

        return newGroupId;
    }
    private String belongTo(String userGroupIds,String groupId){
        //String groupId = user.getGroupId();
        if(userGroupIds != null && !userGroupIds.isEmpty() && !userGroupIds.contains(groupId)){
            userGroupIds += ("|" + groupId);
            if(userGroupIds.endsWith("|")){
                userGroupIds = userGroupIds.substring(0,userGroupIds.length()-1);
            }
            logger.info("1groupId _+_+_+_+_+_+"+userGroupIds );


        }else if(userGroupIds == null || userGroupIds.isEmpty()){
            userGroupIds += (groupId);
            if(userGroupIds.endsWith("\\|")){
                userGroupIds = userGroupIds.substring(0,userGroupIds.length()-2);
            }
            logger.info("2groupId _+_+_+_+_+_+"+userGroupIds );
        }
        return userGroupIds;
    }

    /**
     *
     * @param user 用户信息
     * @param version elastic字段版本，控制并发时候的读写锁
     * @throws VersionConflictEngineException
     */
    private void updateGroupId(User user,long version) throws VersionConflictEngineException{
        logger.info("更新elastic 分组信息-- - --- - - -" + user.getGroupId());
        //elastic.index(User.elc_table_user, new String(data.key()), user);
        Map<String,Object> map = new HashMap<>();
        map.put("groupId",user.getGroupId());
        UpdateRequestBuilder request = CElastic.elastic().client.prepareUpdate(User.elc_index_user, User.elc_table_user, user.getUid()).setVersion(version);
        //request.setParent("16ffd303cf3e459997c0ac9d37496d38");
        request.setDoc(map);
        request.get();
        logger.info("---------更新redis分组信息 -- - --- - - -");
        //更新用户在redis中的分组信息
        String shaMobile = Redis.shard.get("UID|"+user.getUid());
        if(shaMobile != null && !shaMobile.isEmpty()){
            String reidsGroupId = user.getGroupId();
            Redis.shard.hset(shaMobile,StaticParam.userInfo_groupKey,reidsGroupId);
        }else{
            logger.error("用户分组信息不存在。。。。。。。。。。。。啊啊啊啊啊");
        }
    }

    /***
     * 递归判断这个用户是否属于这个组
     * @param user 用户信息
     * @param group 分组
     * @return Sign是否标记
     */
     private static Sign isThisGroup(User user,Group group,Sign sign){
    	//分组单个属性id
     	Integer childGroup = group.getGroup();
     	//属性值
 		String groupValue = group.getGroupValue();
 		//用户分组属性
 		Object attr = null;
 		if(group.getGroupValue() != null){
 			attr = user.getAttr(childGroup);
 		}
    	if(group.getChildren()!=null && group.getChildren().size() > 0){
    		for (Group cgroup : group.getChildren()) {
    			//logger.info("==" + cgroup + "==" +cgroup.getGroup() + "==" +  cgroup.getGroupValue() + " == " +  user.getAttr(cgroup.getGroup()) + " siRight =="+isRight(cgroup.getGroup(), cgroup.getGroupValue(), user.getAttr(cgroup.getGroup())));
    			if(isRight(cgroup.getGroup(), cgroup.getGroupValue(), user.getAttr(cgroup.getGroup()))){
    				//logger.info("--" + user + "--" + cgroup + "--" + sign);
    				isThisGroup(user,cgroup,sign);
    			}
			}
    	}else{//没有子队列则说明是末尾节点，如果末尾满足，则说明符合这个组
    		//logger.info("叶子节点 ----------------------------");
    		if(isRight(childGroup, groupValue, attr)){
    			sign.setRight(true);
    		}
    	}
    	return sign;
    }
    
    /**
     * 判断用户属性是否属于这个属性
     * @param groupType
     * @param groupValue
     * @param userValue
     * @return
     */
    private static boolean isRight(int groupType,String groupValue,Object userValue){
    	//根节点会出现为空的状况
    	if(userValue == null){
    		//logger.info("为空 = = 返回false");
    		return false;
    	}
    	if(groupType == 3){//如果是性别
    		//logger.info("性别   " +userValue + " " + groupValue + " " + String.valueOf(userValue).equals(groupValue));
    		return String.valueOf(userValue).equals(groupValue);
    	}
    	Map<Integer, String> map = NewUserstart.childGroupMap.get(groupType);
    	if(map != null){//字符串情况
    		String string = map.get(Integer.parseInt(groupValue));
    		//logger.info(" userValue=" + userValue + "   groupValue=" + string);
            if(string!=null)
    		if(string.toLowerCase().equals(String.valueOf(userValue).toLowerCase())
                    || string.toLowerCase().contains(String.valueOf(userValue).toLowerCase())
                    || String.valueOf(userValue).toLowerCase().contains(string.toLowerCase())){
    			//logger.info("字符串数据..true.........................................");
    			return true;
    		}
    	}else{//这个情况是区间数据
    		//logger.info("-------区间数据-------");
    		String[] split = groupValue.split(",");
    		if(Double.parseDouble(split[0]) <= Double.parseDouble(String.valueOf(userValue)) && Double.parseDouble(split[1]) >= Double.parseDouble(String.valueOf(userValue))){
    			//logger.info("-------区间数据-------true ------");
    			return true;
    		}
    	}
    	
    	return false;
    }
    

    
    
//    private void sendSmsMessage(int type, String mobile, String code) {
//        String context = MessageStart.TYPE.get(type);
//        if (context != null){
//            if(code!=null) {
//                context = context.replaceAll(MessageStart.CODE_REPAL, code);
//            }
//            System.out.println(context);
//            //SendSMS.sendPhoneSms(context, mobile);
//            logger.error("Successfully type=" + type + " mobile=" + mobile + " context=" + context);
//        }else {
//            logger.error("no this type type=" + type + " mobile=" + mobile + " code=" + code);
//        }
//    }
    
    
    
    
    
    
    
//    public static void main(String[] args) throws SQLException {
//			CElastic.inital(new Config("config/cricle-core.properties"));
//			CElastic elastic2 = CElastic.elastic();
//			User ru = new User();
//			//ru.sId("user_1");
//			ru.setName("zhoujia1");
//			ru.setAge(30);
//			ru.setCity("tianjin1");
//			elastic2.index(StaticParam.elc_table_user, "user_3", ru);
//			
////			GetResponse user = elastic2.get(StaticParam.elc_table_user, "user_1");
////			
////			Object jsonParser = Json.jsonParser(user.getSourceAsString(), RealUser.class);
////			RealUser realUser = (RealUser)jsonParser;
//			
//			ElasticBack<User> back = new ElasticBack<User>(User.class);
//			List<SearchRange> rangeList = new ArrayList<SearchRange>();
//			SearchRange range1 = new SearchRange();
//			range1.setField("city");
//			back.setId_name("setUid");
//			//range1.setFrom("tianjin");
//			//range1.setTo("tianjin");
//			Map<String, String> mustQuery = new HashMap<String, String>();
//			mustQuery.put("city", "tianjin1");
//			rangeList.add(range1);
//			back.setMustnotQuery(mustQuery); 
//			ElasticBack search = elastic2.search(StaticParam.elc_table_user, back);
//			List<User> list = search.getList();
//			System.out.println(list.iterator().next().getName());
    	
//    		long a = System.currentTimeMillis();
//			User user = new User();
//			
//			user.setName("zhoujia");
//			user.setSex(0);
//			user.setCity("北京");
//    		user.setQnum(20);
//    		
//    		UserGroupConsumer ugc = new UserGroupConsumer(null, 0, null);
//    		Group rootGroup = new Group();
//    		rootGroup.setGroupId(1);
//    		Group group = new UserDao().getGroups(rootGroup);
//    		Sign sign = new Sign();
//    		ugc.isThisGroup(user, group,sign);
//    		long b = System.currentTimeMillis();
//    		System.out.println("iiiiiiiiii="+sign.isRight);
//    		System.out.println(b-a);
    		
//	}
    
//    public static void main(String[] args) {
//    	Map<Integer, Map<Integer, String>> childGroupMap = new HashMap<Integer,  Map<Integer, String>>();
//    	Map<Integer, String> qudao = new HashMap<Integer, String>();
//    	qudao.put(1, "App Store");
//    	qudao.put(2, "Google Play");
//    	
//    	Map<Integer, String> systemMap = new HashMap<Integer, String>();
//    	systemMap.put(1, "iOS");
//    	systemMap.put(2, "android");
//    	
//    	Map<Integer, String> sexMap = new HashMap<Integer, String>();
//    	sexMap.put(0, "男");
//    	sexMap.put(1, "女");
//    	
//    	Map<Integer, String> cityMap = new HashMap<Integer, String>();
//    	cityMap.put(1, "北京");
//    	cityMap.put(2, "上海");
//    	cityMap.put(3, "天津");
//    	cityMap.put(4, "郑州");
//    	cityMap.put(5, "广州");
//    	//。。。以上数据未完整
//    	childGroupMap.put(1, qudao);
//    	childGroupMap.put(2, systemMap);
//    	childGroupMap.put(3, sexMap);
//    	childGroupMap.put(4, cityMap);
//    	NewUserstart.childGroupMap = childGroupMap;
//    	try {
//    		Config config = new Config(StaticParam.config_path + "group_conf.properties");
//    		CElastic.inital(config);
//			CElastic elastic = CElastic.elastic();
//			GetResponse userResponse = elastic.get(User.elc_table_user, new String("54cebb9c3996428e97ff0cd49fc4e93f"));
//			String userJson = userResponse.getSourceAsString();
//			
//			logger.info("取到的用户信息 = " + userJson);
//			//{"imei":null,"ip":null,"pass":"123456","name":null,"rname":null,"card":null,"sex":"0","device":"4080c65d1f1374202057fdd4b3599c79","uurl":"","curl":null,"purl":null,"company":null,"auth":null,"iscom":0,"comdate":null,"isava":0,"avadate":null,"age":0,"city":null,"remark":null,"mobile":"18500420070","cdate":1446190889963,"sdate":0,"lstime":1446190889963,"ascore":5.0,"att":0.0,"deep":0.0,"speed":0.0,"cid":"4080c65d1f1374202057fdd4b3599c79","incr":"1","anum":0,"qscore":5.0,"qnum":0,"weixin":null,"qq":null,"email":null,"system":"ios","channel":"4080c65d1f1374202057fdd4b3599c79","totalPay":null,"totalIncome":null,"aliacc":null,"alimob":null,"drawpwd":null,"totalGetMoney":null,"totalGetMoneyNum":null,"nowMoney":null,"lastAnsTime":null,"lastQuesTime":null,"price":"0,999999999","pub":"1","day":"0|1|2|3|4|5|6","time":"0000,2359","access":0,"groupId":"","staticGroupId":"","larenGroupId":"","sound":0,"posxy":"4080c65d1f1374202057fdd4b3599c79","rcity":"4080c65d1f1374202057fdd4b3599c79","nn":"格雷"}
//			User user = (User)Json.jsonParser(userJson, User.class);
//			
//			Redis.initialShard(config);
//			Redis.initial(config);
//			String string = Redis.shard.get("condition_group_161");//158 女（android)   161 男（iOS）  164（iOS）
//			//{"groupId":164,"groupType":null,"parentId":null,"groupValue":null,"group":null,"children":[{"groupId":165,"groupType":1,"parentId":164,"groupValue":"1","group":3,"children":[{"groupId":166,"groupType":2,"parentId":165,"groupValue":"1","group":2,"children":null}]}]}
//			//{"groupId":161,"groupType":null,"parentId":null,"groupValue":null,"group":null,"children":[{"groupId":162,"groupType":1,"parentId":161,"groupValue":"0","group":3,"children":[{"groupId":163,"groupType":2,"parentId":162,"groupValue":"1","group":2,"children":null}]}]}
//			//{"groupId":158,"groupType":null,"parentId":null,"groupValue":null,"group":null,"children":[{"groupId":159,"groupType":1,"parentId":158,"groupValue":"1","group":3,"children":[{"groupId":160,"groupType":2,"parentId":159,"groupValue":"2","group":2,"children":null}]}]}
//			logger.info("string . . . . = = " + string);
//			Group group = (Group)Json.jsonParser(string, Group.class);
//			Sign sign = new Sign();
//			isThisGroup(user, group,sign);
//			logger.info("是否属于这个组 = " + sign.isRight );
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
    

}
