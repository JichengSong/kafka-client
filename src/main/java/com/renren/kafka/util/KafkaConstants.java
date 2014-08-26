package com.renren.kafka.util;


/**
 * TODO kafka客户端需要的常量信息
 * 
 * @author xiaoqiang, created at 2014年8月7日 上午10:27:52
 */
public class KafkaConstants {

    public static final String KAFKA_CONFIG_FILE = "kafka-config.properties";

    public static final long CONSUMER_RECONNECT_INTERVAL_MS = 10000; // 10sec

    /**
     * The sms notification api url
     */
    public static final String XIAONEI_SMS_URL = "http://sms.notify.d.xiaonei.com:2000/receiver";
    
    public static final String MAIL_SMTP_USER = "mail.smtp.user";   // 发送方邮件地址。
    public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";  // 邮件密码。
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";  // smtp主机名。
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";  
    
}
