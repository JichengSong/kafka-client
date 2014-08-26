package com.renren.kafka.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.renren.kafka.exception.InfraKafkaException;

/**
 * 有效性检查公用类。
 * 
 * @author yueqiang.zheng<tjuxiaoqiang@163.com>
 * @since 2014-2-7
 */
public class Validators {
	public static final String VALID_PATTERN_STR = "^[a-zA-Z0-9_-]+$";
    public static final int CHARACTER_MAX_LENGTH = 255;
    
    /**
     * 通过正则表达式进行字符匹配
     * 
     * @param origin
     * @param patternStr
     * @return
     */
    public static boolean regularExpressionMatcher(String origin, String patternStr) {
        if (StringUtils.isBlank(origin)) {
            return false;
        }
        if (StringUtils.isBlank(patternStr)) {
            return true;
        }
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(origin);
        return matcher.matches();
    }
    
    /**
     * topic 有效性检查
     * 
     * @param topic
     * @throws InfraKafkaException
     */
    public static void checkTopic(String topic) throws InfraKafkaException {
        if (StringUtils.isBlank(topic)) {
            throw new InfraKafkaException("the specified topic is blank", null);
        }
        if (!regularExpressionMatcher(topic, VALID_PATTERN_STR)) {
            throw new InfraKafkaException(String.format(
                "the specified topic[%s] contains illegal characters, allowing only %s", topic,
                VALID_PATTERN_STR), null);
        }
        if (topic.length() > CHARACTER_MAX_LENGTH) {
            throw new InfraKafkaException("the specified topic is longer than topic max length 255.", null);
        }
    }

    /**
     * message 有效性检查
     * 
     * @param topic
     * @param msgData
     * @throws InfraKafkaException
     */
    public static void checkMessage(String topic, String msgData)
            throws InfraKafkaException {
        if (null == msgData) {
            throw new InfraKafkaException("the message is null", null);
        }
        // topic
        Validators.checkTopic(topic);
        if (0 == msgData.length()) {
            throw new InfraKafkaException("the message body length is zero", null);
        }
    }

}
