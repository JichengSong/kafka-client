/**
 * <p> @(#)KafkaUtil.java, 2013-9-18. </p>
 * 
 * Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.kafka.util;

/**
 *
 * @author wmc
 *
 */
public class InfraKafkaUtil {
    public static final String KAFKA_CONFIG_OFFSET_MYSQL = "MYSQL";
    public static final String KAFAK_CONFIG_OFFSET_FILE = "FILE";
    public static final int KAFKA_BTP_UPDATE_GAP = 60;
    public static final int KAFKA_LOW_CONSUMER_CONNECT_TIMEOUT = 100000;
    public static final int KAFKA_LOW_CONSUMER_BUFFERSIZE = 1024;
    public static final int KAFKA_LOW_CONSUMER_CHANGE = 10;
    public static final int KAFKA_LOW_CONSUMER_REQUEST_ERROR = 10;
    public static final String BROKER_IDS_PATH = "/brokers/ids";
    public static final String BROKER_TOPICS_PATH = "/brokers/topics";
    public static final String CONSUMERS_PATH = "/consumers";
    public static final String KAFKA_WHITELIST = "/whitelist";
    public static final String KAFKA_WLIST_NAME = "whitelist";
    
    public static final int PARTITIONNUMBER = 10;
    // Redis
    public static final int REDIS_API_STATISTICS_EXPIRE = 120;

    public static final boolean AUTOLOAD_CONFIG = true;
    // Kafka

    // Mail Alarm
    public static final String ALARM_SMTP_SERVER = "smtp.renren.com";

    public static final String ALARM_SMTP_USERNAME = "yueqiang.zheng";

    public static final String ALARM_SMTP_PASSWORD = "ZhYQ19851105";

    // Alarm Threshold
    public static final int ALARM_API_COUNT_THRESHOLD = 100;

    public static final int ALARM_API_MAX_TIME_THRESHOLD = 200;

    public static final int ALARM_API_AVG_TIME_THRESHOLD = 150;

    
    // Alarm
    public static final String CK_MAILLIST = "yueqiang.zheng@renren-inc.com";
    public static final String CK_PHONELIST = "18698054112";
    public static final String CK_ALARMTITLE = "Kafka alram";
}
