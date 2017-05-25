package com.circle.netty.formation.message.model;


import com.circle.core.util.Verification;
import com.circle.netty.http.JsonParams;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author Created by cxx on 15-9-4.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SysMessage {

    public static final String SYSMSG = "系统消息";
    public static final String TASK_MSG = "任务消息";
    public static final String SYS_LOG = "SYS_LOG";

    /**首次登录后，获得系统消息 S */
    public static final String first_sgin = "欢迎来到“问啊”，（广告法不能说的）程序员技能分享平台。";
    /**发题人，发布问题有新抢答者 S */
    public static final String que_have_rober = "您发布的题目又有新的抢答者，请您在规定时间内进行筛选。";
    /**发题人，题目快到时间，提醒是否延时*/
    public static final String que_can_delay = "您发布的题目即将完成，请确定是否需要延时。";
    /**发题人，题目进入到筛选 */
    public static final String que_into_chose = "您发布的问答报名名额已满或发题时间已到，自动进入到筛选阶段，请您在规定时间内进行筛选。";
    /**发题人，题目无人抢答，自动取消*/
    public static final String que_no_rober = "您发布的题目在规定报名时间内无人抢答，尝试提高价格，增加抢答机会。";
    /**发题人，题目有人抢答，未筛选，自动取消*/
    public static final String que_no_chose = "您发布的题目在规定报名时间内，未选择答题人，题目已经自动取消。";
    /**发题人，题目进入到进行阶段	*/
    public static final String que_into_answer = "您已完成答题人筛选，有问题赶紧去“问啊”～";
     /**发题人，题目进入到延时阶段	*/
     public static final String que_into_delay = "对方已经接受了您的延时申请，请尽快完成问答。";
    /**发题人，题目进入到评价阶段	*/
    public static final String que_into_app = "您的问答已完成，给对方一个好评吧！您的评价是Ta前进的动力。";
    /**发题人，对方拒绝延时	*/
    public static final String que_no_delay = "对方已经拒绝了您的延时申请。";
    /**发题人，对方已评价	*/
    public static final String que_ready_app = "对方已经对您做出了评价，如您还没有给对方作出评价，请及时评价。";
    /**答题人，收到问题推送 T	 【问啊】提醒：有一条适合作答的问题，请速去来抢 */
    public static final String ans_new_que = "题目：\"";
    /**答题人，题目报名	*/
    public static final String ans_ready_rob = "您已经报名了该问题，对方将会在报名已满或报名时间截止后进行筛选，请您随时关注进度。";
    /**答题人，题目已入围，进入到进行阶段	*/
    public static final String ans_rob_yes = "您已经被发题人选做了该问题的回答对象，请及时与对方联系。";
    /**答题人，题目未入围	*/
    public static final String ans_rob_no = "很遗憾，发题人已选他人作答。勿灰心，平台上还有更多的问答在等着您！";
    /**答题人，对方申请延时	T|S */
    public static final String ans_req_delay = "对方需要更多的时间完成问答，申请延时，请您确认。";
    /**答题人，题目完成，进入评价阶段	*/
    public static final String ans_into_app = "您的问答已完成，对方的酬金已打入您的“问啊”账户中，您可以给对方作出评价或继续参与其他问答。";
    /**答题人，题目进入到延时阶段	*/
    public static final String ans_info_delay = "您已确认对方的申请延时，酬金已打入您的“问啊”账户中，请注意查收！";
    /**答题人，对方已评价	*/
    public static final String ans_queapp = "对方已经对您作出了评价，赶紧去看看吧！如您还没有给对方作出评价，请及时给予评价。";
    /**答题人，已报名，对方取消问题  T|S	*/
    public static final String ans_rob_cencel = "您抢答的问题，对方已经取消。";
    /**提现错误5次	*/
    public static final String sys_draw_five = "您今日已经预申请提现时输入错误五次密码，系统将锁定到下个提现日。";
    /**支付宝绑定	您当前已绑定支付宝账号XXXXX */
    public static final String sys_alipay_bind = "您当前已绑定支付宝账号";
    /**被举报	*/
    public static final String que_about_complain = "您的资料或发题描述等被人举报了，请第一时间进行修改";
    /**发题人，投诉处理结果	*/
    public static final String que_about_complain_succ = "您的投诉已有结果了，您的悬赏金，已经打回到您的账户";
    /**发题人，投诉处理结果	*/
    public static final String que_about_complain_fail = "您的投诉已有结果了，您的悬赏金，已经打给对方";
    /**答题人，被投诉	*/
    public static final String ans_about_complain = "对方对您的服务进行了投诉，投诉结果将于近期公布";
    /**答题人，投诉处理结果	*/
    public static final String ans_about_complain_succ = "对方的投诉已有结果，悬赏金已经打入到您的账户，请注意查收";
    /**答题人，投诉处理结果	*/
    public static final String ans_about_complain_fail = "对方的投诉已有结果，悬赏金已经返还给对方，请您下次注意服务态度和服务质量";
    /**提现通过	*/
    public static final String sys_about_draw_succ = "您的提现已通过，我们将于一个工作日内打入到您的支付宝中，请注意查收";
    /**提现未通过	*/
    public static final String sys_about_draw_fail = "您的提现中涉及到非法抢单，抢单金额已扣除，其余金额已返回到您的账户中，望您遵守互联网秩序，净化网络环境";
    /**获得红包	*/
    public static final String sys_about_red = "您已获得一个推送红包，可从您的优惠券中查看";
    /**答题人 ， 您回答的问题，对方已经取消*/
    public static final String ans_que_cencal = "您回答的问题，对方已经取消。";

    public static final int type_sys = 1;/*系统消息*/
    public static final int type_task =2;/*任务消息*/
    public static final int about_sys = 0;/*系统消息-不做任何处理*/
    public static final int about_classify = 1;/*认证*/
    public static final int about_question = 2;/*关于问题*/
    public static final int about_report =3;/*关于举报*/
    public static final int about_url = 5;/*url跳转*/
    public static final int about_draw = 6;/*关于提现*/
    public static final int about_vimg = 7;/*关于照片认证*/
    public static final int about_vcompany = 8;/*关于公司认证*/
    public static final int about_complain = 9;/*关于投诉*/
    public static final int about_advice = 10;/*关于意见反馈*/
    private int type;
    /**
     * 关于-问题-优惠券-资金-投诉-举报
     */
    private int about;
    /**
     * 标题
     */
    private String title="";
    /**
     * 消息内容
     */
    private String context="";
    /**
     * 图标
     */
    private String logo="";
    /**
     * 问题ID
     */
    private String qid="";
    private String url="";
    private long time;
    /**问答人  1 答题人 2 提问人*/
    private int aq;
    public static SysMessage create(String value) {
        String[] vs = value.split(JsonParams.SPLIT_BACK);
        if (vs.length < 6) return null;
        SysMessage msg = new SysMessage();
        msg.setType(Integer.valueOf(vs[0]));
        msg.setAbout(Integer.valueOf(vs[1]));
        msg.setTitle(vs[2]);
        msg.setContext(vs[3]);
        msg.setLogo(vs[4]);
        msg.setQid(vs[5]);
        if(vs.length >6)
            msg.setUrl(vs[6]);
        if(vs.length >7)
            msg.setTime(Verification.getLong(0,vs[6]));
        return msg;
    }

    public String create() {
        return type +
                JsonParams.SPLIT + about +
                JsonParams.SPLIT + title +
                JsonParams.SPLIT + context +
                JsonParams.SPLIT + logo +
                JsonParams.SPLIT + qid +
                JsonParams.SPLIT + url +
                JsonParams.SPLIT + time;
    }

    public int getAq() {
        return aq;
    }

    public void setAq(int aq) {
        this.aq = aq;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAbout() {
        return about;
    }

    public void setAbout(int about) {
        this.about = about;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }
}
