/**
 * <p> @(#)KafkaScheduler.java, 2013-9-18. </p>
 *
 * Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.kafka.exception;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.renren.kafka.log.ClientLogger;


/**
 * @author wmc
 */
public class InfraKafkaScheduler {

    private static final Logger logger = ClientLogger.getLog();

    private static InfraKafkaScheduler instance = new InfraKafkaScheduler();

    private HashMap<String, SchedulerKernel> data = new HashMap<String, SchedulerKernel>();

    private InfraKafkaScheduler() {
        // singleton
    }


    public static InfraKafkaScheduler getInstance() {
        return instance;
    }

    public InfraKafkaScheduler add(Runnable runnable, int threadNumber, long schedulerTime) {
        if (!data.containsKey(runnable.getClass().toString())) {
            SchedulerKernel sample = new SchedulerKernel();
            sample.scheduleAtFixedRate(runnable, threadNumber, schedulerTime);
            data.put(runnable.getClass().toString(), sample);
            logger.info("Runnable " + runnable.toString() + "  is added");
        } else {
            logger.info("Runnable " + runnable.toString() + " has been added, ignore");
        }

        return this;
    }

    public void shutdown(Runnable runnable) {
        if (data.containsKey(runnable.getClass().toString())) {
            data.get(runnable.getClass().toString()).shutdown();
        } else {
            logger.warn(runnable.getClass().toString() + " doesn't exist, ignore shutdown");
        }
    }

    class SchedulerKernel {
        private ScheduledExecutorService kafkaScheduler;

        public void scheduleAtFixedRate(Runnable runnable, int threadNumber, long schedulerTime) {
            if (kafkaScheduler == null) {
                synchronized (InfraKafkaScheduler.class) {
                    kafkaScheduler = Executors.newScheduledThreadPool(threadNumber, new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable runnable) {
                            Thread thread = new Thread(runnable, runnable.toString());
                            thread.setDaemon(true);
                            return thread;
                        }
                    });
                }
                kafkaScheduler.scheduleAtFixedRate(runnable, schedulerTime, schedulerTime, TimeUnit.SECONDS);
            }
        }

        public void shutdown() {
            logger.info(kafkaScheduler.toString() + " begin to shutdown");
            kafkaScheduler.shutdownNow();
        }
    }
}
