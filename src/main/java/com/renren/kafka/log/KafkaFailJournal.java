/*
 * Copyright 2014 Renren.com All right reserved. This software is the
 * confidential and proprietary information of Renren.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Renren.com.
 */
package com.renren.kafka.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;


/**
 * TODO describe the implementation of class
 * @author xiaoqiang, created at 2014年8月7日 下午3:42:21
 */
public class KafkaFailJournal extends Journal<KafkaMessage> {
    private static String SEND_FAIL_MESSAGE_FILE;
    
    public static final KafkaFailJournal journal = new KafkaFailJournal();
    
    public static KafkaFailJournal getInstance() {
        return journal;
    }
    
    private static BufferedWriter writer;
    
    private static AtomicLong num = new AtomicLong(0);
    
    private KafkaFailJournal() {
        super();
        KafkaFailJournal.SEND_FAIL_MESSAGE_FILE = props.getProperty("send.fail.message.file", "/data/logs/kafkalogs/faillog");
        try {
            KafkaFailJournal.writer = new BufferedWriter(new FileWriter(SEND_FAIL_MESSAGE_FILE, true));
        } catch (IOException e) {
            logger.error("open " + SEND_FAIL_MESSAGE_FILE + "error.", e);
        }
    }

    /* (non-Javadoc)
     * @see com.renren.ugc.comment.kafka.log.Journal#init()
     */
    @Override
    protected void init() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.renren.ugc.comment.kafka.log.Journal#process(java.lang.Object)
     */
    @Override
    protected void process(KafkaMessage val) {
        String message = null;
        if (val != null) {
            message = JSON.toJSONString(val);
        }
        
        try {
            num.incrementAndGet();
            writer.write(message);
            writer.newLine();
            if (num.longValue() % 500 == 0) {
                writer.flush();
            }
        } catch (Throwable e) {
            logger.error("write " + SEND_FAIL_MESSAGE_FILE + "error.", e);
        }
        
    }

    /* (non-Javadoc)
     * @see com.renren.ugc.comment.kafka.log.Journal#close()
     */
    @Override
    protected void close() {
        try {
            writer.flush();
        } catch (Throwable e) {
            logger.error("flush " + SEND_FAIL_MESSAGE_FILE + "error.", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("close " + SEND_FAIL_MESSAGE_FILE + "error.", e);
            }
        }
        
    }

}
