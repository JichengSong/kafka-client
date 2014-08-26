/*
 * Copyright 2014 Renren.com All right reserved. This software is the
 * confidential and proprietary information of Renren.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Renren.com.
 */
package com.renren.kafka.log;

import java.util.List;




/**
 * TODO describe the implementation of class
 * @author xiaoqiang, created at 2014年8月7日 下午8:07:15
 */
public class KafkaMessage {
    /**
     * 消息的topic
     */
    private String topic;
    
    private String key;
    
    private List<String> messages;

    
    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    
    /**
     * @param topic the topic to set
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    
    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    
    /**
     * @return the messages
     */
    public List<String> getMessages() {
        return messages;
    }

    
    /**
     * @param list the messages to set
     */
    public void setMessages(List<String> list) {
        this.messages = list;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "KafkaMessage [topic=" + topic + ", key=" + key + ", messages="
               + messages + "]";
    }

}
