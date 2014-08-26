package com.renren.kafka.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.log4j.Logger;

import com.renren.kafka.log.ClientLogger;



/**
 * 发送邮件的接口
 * 
 * @author xiaoqiang, created at 2014年8月7日 上午11:26:57
 */
public class EmailUtils {
    private static final Logger logger = ClientLogger.getLog();

    private static EmailUtils instance = new EmailUtils();
    
    private Properties props = KafkaConfigLoader.loadPropertyFile(
        this.getClass().getClassLoader().getResourceAsStream(KafkaConstants.KAFKA_CONFIG_FILE));

    public static EmailUtils getInstance() {
        return instance;
    }

    private EmailUtils(){
    }

    /**
     * 发送邮件的接口
     * 
     * @param receiverListStr 接收者列表，如fu.li@opi-corp.com,xiaojie.bai@opi-corp.com
     * @param ccListStr 抄送列表，如fu.li@opi-corp.com,xiaojie.bai@opi-corp.com
     * @param messageBody 邮件主题内容
     * @param title 邮件标题
     */
    public void sendEmail(String title, String messageBody,
        String receiverListStr, String ccListStr) {
        if (props == null) {
            logger.error("mail properties is error.");
            return;
        }
        try {
            PopupAuthenticator popA = new PopupAuthenticator();// 邮件安全认证。
            popA.performCheck(props.getProperty(KafkaConstants.MAIL_SMTP_USER), props.getProperty(KafkaConstants.MAIL_SMTP_PASSWORD)); // 填写用户名及密码
            Session sendMailSession = Session.getInstance(props, popA);
            
            Message newMessage = new MimeMessage(sendMailSession);
            newMessage.setFrom(new InternetAddress(props.getProperty(KafkaConstants.MAIL_SMTP_USER)));
            InternetAddress[] addressList =
                    InternetAddress.parse(receiverListStr);
            newMessage.setRecipients(Message.RecipientType.TO, addressList); // 接收方邮件地址
            Calendar ca = Calendar.getInstance();
            ca.setTime(new java.util.Date());
            newMessage.setSubject(title);
            newMessage.setSentDate(new Date());

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageBody, "text/html;charset=gbk");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            newMessage.setContent(multipart);
            // 添加抄送
            if (ccListStr != null && ccListStr.trim().length() > 0) {
                String[] ccAddressList = ccListStr.split(",");
                InternetAddress[] ccList =
                        new InternetAddress[ccAddressList.length];
                for (int i = 0; i < ccAddressList.length; i++) {
                    ccList[i] = new InternetAddress(ccAddressList[i]);
                }
                newMessage.setRecipients(Message.RecipientType.CC, ccList);
            }
            Transport transport = sendMailSession.getTransport("smtp");
            transport.send(newMessage);
        } catch (MessagingException ex) {
            logger.error("Send Email fail.", ex);
        }
    }

    public class PopupAuthenticator extends Authenticator {

        String username = null;
        String password = null;

        public PopupAuthenticator(){
        }

        public PasswordAuthentication performCheck(String user, String pass) {
            username = user;
            password = pass;
            return getPasswordAuthentication();
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    public static void main(String a[]) {
        String title = "Kafka error";
        String messageBody = "messageBody";
        String receiverListStr = "yueqiang.zheng@renren-inc.com";
        EmailUtils.getInstance().sendEmail(title, messageBody, receiverListStr,
            null);
    }
}
