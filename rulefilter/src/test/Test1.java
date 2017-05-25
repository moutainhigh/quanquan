package test;

import com.circle.core.elastic.Json;
import com.rulesfilter.yy.zj.model.AnsLog;
import com.rulesfilter.yy.zj.model.FilterRule;
import com.rulesfilter.yy.zj.model.ProcessFilterRule;
import com.rulesfilter.yy.zj.model.QuestionLog;
import com.rulesfilter.yy.zj.subscrib.Subscribe;

/**
 * @author zhoujia
 *
 * @date 2015年8月12日
 */
public class Test1 {

	public static void main(String[] args) {
//		Subscribe ss = new Subscribe();
//		
//		ss.subscribeDelKey();
//		FilterRule fr = new ProcessFilterRule();
		String qid = "f9fbd7c6c4d2410d82bcf97a5e530f3b";
		AnsLog log = new AnsLog();
		
		log.setAnsTime(1231321321);
		log.setAnswerAccount("zhoujia_aaa");
		log.setAnswerIMEI("1231sadfasdf");
		log.setAnswerIP("10.2.138.8");
		log.setAnswerPhone("18612419694");
		log.setCash("123123");
		log.setLogId("123123");
		log.setQid("SDFS23DSF234r5SD");
		log.setQuestionAccount("AAAAA_account");
		log.setQuestionIMEI("AAAA-IMEI");
		log.setQuestionIP("10.2.2.2");
		log.setQuestionPhone("1321231456");
		log.setQueUserId("asfkslfdjaisdfjklafj1231");
		log.setRuleContentNum(123);
		log.setSelectTime(123);
		log.setTime(System.currentTimeMillis());
		log.setUserId("asfsadfjaskdf88asdfsaf8787dfgasdfj");
		log.setHit("1");
		
		
		QuestionLog qLog = new QuestionLog();
		qLog.setBeginAnswerTime(123123123123123L);
		qLog.setEndAnswoerTime( 123123123131278L);
		qLog.setBeginSlectTime(123123123123L);
		qLog.setEndSlectTIme(123123456456L);
		qLog.setQuestionId(qid);
		
		String json = Json.json(qLog);
		System.out.println(json);

	}

}
