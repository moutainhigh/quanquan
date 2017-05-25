package com.circle.task.ruzhang.util;

import com.circle.core.util.Config;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * set from=service@quanquanhd.com
 * set smtp=smtp.exmail.qq.com
 * set smtp-auth-user=service@quanquanhd.com
 * set smtp-auth-password=kFxJFJCV7Xw9FyJkwVZb
 * @author Created by cxx on 15-11-4.
 */
public class EmailUtils {
    public static String username;
    public static String passwd;
    public static String host;
    public static String port;
    public static String from;
    public static String to;
    public static String title;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static void intail(Config config){
        username = config.getAsString("mail.smtp.username");
        passwd = config.getAsString("mail.smtp.passwd");
        host = config.getAsString("mail.smtp.host");
        port = config.getAsString("mail.smtp.port");
        from = config.getAsString("mail.smtp.from");
        to = config.getAsString("mail.smtp.to");
        title = config.getAsString("mail.smtp.title");
    }
    public static void sendEmailAsk(String title,StringBuffer context) {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(username);
        mailInfo.setPassword(passwd);//您的邮箱密码
        mailInfo.setFromAddress(from);
        mailInfo.setSubject(title + " [" + dateFormat.format(System.currentTimeMillis())+"]");
        mailInfo.setContent(context.toString());
        String[] toemail = to.split(",");
        for (String email:toemail){
            mailInfo.setToAddress(email);
            //这个类主要来发送邮件
            SimpleMailSender.sendTextMail(mailInfo);//发送文体格式
        }
    }
    public static void sendEmailAsk(StringBuffer context) {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(username);
        mailInfo.setPassword(passwd);//您的邮箱密码
        mailInfo.setFromAddress(from);
        mailInfo.setSubject(title + " [" + dateFormat.format(System.currentTimeMillis())+"]");
        mailInfo.setContent(context.toString());
        String[] toemail = to.split(",");
        for (String email:toemail){
            mailInfo.setToAddress(email);
            //这个类主要来发送邮件
            SimpleMailSender.sendTextMail(mailInfo);//发送文体格式
        }
    }
    public static void sendEmailhtmlAsk(StringBuffer context) {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        mailInfo.setUserName(username);
        mailInfo.setPassword(passwd);//您的邮箱密码
        mailInfo.setFromAddress(from);
        mailInfo.setSubject(title  + " [" + dateFormat.format(System.currentTimeMillis()) + "]");
        mailInfo.setContent(context.toString());
        String[] toemail = to.split(",");
        for (String email:toemail){
            mailInfo.setToAddress(email);
            //这个类主要来发送邮件
            SimpleMailSender.sendTextMail(mailInfo);//发送文体格式
        }
    }

    public static void main(String[] args) throws IOException {
        intail(new Config("config/app.properties"));
        sendEmailAsk(new StringBuffer("alksjdlaksdjlaksjdlakdsjl"));
    }
}
