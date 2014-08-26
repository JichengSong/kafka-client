/*
 * Copyright 2014 Renren.com All right reserved. This software is the
 * confidential and proprietary information of Renren.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Renren.com.
 */
package com.renren.kafka.log;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.renren.kafka.util.KafkaConfigLoader;
import com.renren.kafka.util.KafkaConstants;


/**
 * TODO describe the implementation of class
 * 
 * @author xiaoqiang, created at 2014年8月7日 下午2:37:40
 */
public abstract class Journal<I> {
    protected static final Logger logger = ClientLogger.getLog();
    
    /**
     * Queue for pending input.
     */
    protected BlockingQueue<I> inputQueue;
    
    /**
     * Read write Lock used to avoid the read and write conflict.
     */
    private ReadWriteLock lock;
    
    /**
     * Contain config Info
     */
    protected static Properties props;
    
    
    public Journal() {
        this.inputQueue = new LinkedBlockingQueue<I>();
        this.lock = new ReentrantReadWriteLock();
        Journal.props = KafkaConfigLoader.loadPropertyFile(
            this.getClass().getClassLoader().getResourceAsStream(KafkaConstants.KAFKA_CONFIG_FILE));
    }
    
    /**
     * 向队列中添加需要持久化的值
     * 
     * @param val
     * @throws InterruptedException
     */
    public void put(I val) throws InterruptedException {
        this.inputQueue.put(val);
    }
    
    /**
     * 清空队列中的内容
     */
    public void clean() {
        this.lock.writeLock().tryLock();
        try {
            this.inputQueue.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public void execute() {
        init();
        I val;
        try {
            val = inputQueue.take();
            while (true) {
                process(val);
                val = inputQueue.take();
            }
        } catch (Throwable e) {
            logger.error("Journal Worker process fail.", e);
        } finally {
            close();
        }
    }
    
    /**
     * 启动执行线程
     */
    public void start() {
        Thread thread = new Thread(new Worker());
        thread.start();
    }
    
    /**
     * 
     */
    protected abstract void close();

    /**
     * @param val
     */
    protected abstract void process(I val);

    /**
     * 初始化相应的处理类
     */
    protected abstract void init();

    /**
     * 工作线程
     * 
     * @author xiaoqiang, created at 2014年8月7日 下午3:09:13
     */
    private class Worker implements Runnable {

        public void run() {
            try {
                logger.info(this.getClass().getName() + "start!");
                execute();
            } catch(Throwable e) {
                logger.error("Journal Worker execute fail.", e);
            }
        }
    }

}
