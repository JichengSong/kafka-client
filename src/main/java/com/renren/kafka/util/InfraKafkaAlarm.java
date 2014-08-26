/**
 * <p> @(#)KafkaAlarm.java, 2013-9-18. </p>
 * 
 * Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.kafka.util;

import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.renren.kafka.log.ClientLogger;



/**
 * Kafka client alerm tool class
 * 
 * @author xiaoqiang, created at 2014年8月7日 下午2:11:17
 */
public class InfraKafkaAlarm {

    private static final Logger logger = ClientLogger.getLog();
	private static InfraKafkaAlarm uniqueInstance = new InfraKafkaAlarm();
	
	Properties props = KafkaConfigLoader.loadPropertyFile(
        this.getClass().getClassLoader().getResourceAsStream(KafkaConstants.KAFKA_CONFIG_FILE));

    private static final String reqUrl = KafkaConstants.XIAONEI_SMS_URL;
	private static String[] phoneList = InfraKafkaUtil.CK_PHONELIST.split(",");
	private static final String title = InfraKafkaUtil.CK_ALARMTITLE;
    private static String[] emailList = InfraKafkaUtil.CK_MAILLIST.split(",");
    
	private InfraKafkaAlarm() {
	    InfraKafkaAlarm.emailList = props.getProperty("mail.alerm.emailList", InfraKafkaUtil.CK_MAILLIST).split(",");
	    InfraKafkaAlarm.phoneList = props.getProperty("mail.alerm.phoneList", InfraKafkaUtil.CK_PHONELIST).split(",");
	}

	public static InfraKafkaAlarm getInstance() {
		return uniqueInstance;
	}
	
	public InfraKafkaAlarm sendEmailAlarm(String content) {
		for (String item:emailList) {
			EmailUtils.getInstance().sendEmail(title, content, item,
		            null);
		}
		return this;
	}

	public InfraKafkaAlarm sendSMSAlarm(String content) {
        for (String item: phoneList) {
        	HttpUtils.sendGet(reqUrl, splice(item, content));
            if (logger.isDebugEnabled()) {
        	    logger.debug("send message to " + item + " successfully!");
            }
		}
    	return this;
	}
	
	/**
	 * Description:
	 * 	splice the send message content 
	 * @param number
	 * @param content
	 * @return
	 */
	private String splice(String number, String content)
	{
		return "number="+number+"&message="+URLEncoder.encode(content);
	}
}
