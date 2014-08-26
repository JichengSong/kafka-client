/*
 * Copyright 2013 Renren.com All right reserved. This software is the
 * confidential and proprietary information of Renren.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Renren.com.
 */
package com.renren.kafka.exception;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.renren.kafka.log.ClientLogger;
import com.renren.kafka.util.InfraKafkaAlarm;

/**
 *
 * @author wmc
 *
 */
public class InfraKafkaExceptionMonitor {

	private static final Logger logger = ClientLogger.getLog();

	private AtomicInteger exceptionCount = new AtomicInteger(0);

	private boolean suspend = false;

    /**
     * 重连时间
     */
	private static int reConnectionTime;

    /**
     * 异常个数重置时间
     */
	private static int cleanExceptionTime;

    /**
     * 异常名称
     */
	private static String exceptionName;

    /**
     * 异常总数
     */
	private static int totalException;

	public void start(int reConnectionTime, int cleanExceptionTime,
			int totalException, String exceptionName) {
		InfraKafkaExceptionMonitor.reConnectionTime = reConnectionTime;
		InfraKafkaExceptionMonitor.cleanExceptionTime = cleanExceptionTime;
		InfraKafkaExceptionMonitor.exceptionName = exceptionName;
		InfraKafkaExceptionMonitor.totalException = totalException;
		InfraKafkaScheduler.getInstance().add(new CleanException(), 1,
				InfraKafkaExceptionMonitor.cleanExceptionTime).add(new ReConnection(), 1,
				InfraKafkaExceptionMonitor.reConnectionTime);
	}

	public boolean isSuspend() {
		return suspend;
	}

	/**
	 * Description:
	 *  get exception to handler
	 * @param e
	 * @param alarmString
	 */
	public void recordException(InfraKafkaException e, String alarmString) {
		if (exceptionCount.incrementAndGet() > totalException) {
			logger.error(
					String.format(
							"MQ send error:%s, currentErrorCount=%s, errorCountMax=%s ",
							e.getMessage(), exceptionCount.get(),
							totalException), e);
			suspend = true;

			// TODO: send alarm
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String content = String.format(
                "Time: %s MQ send error:%s, currentErrorCount=%s, errorCountMax=%s ",
                sdf.format(new Date(System.currentTimeMillis())), e.getMessage(), exceptionCount.get(),
                totalException);
			InfraKafkaAlarm.getInstance().sendEmailAlarm(content).sendSMSAlarm(content);
			exceptionCount.set(0);
		}
	}



	class ReConnection implements Runnable {

		@Override
		public void run() {
			logger.info(exceptionName + " reconnect -----------------------");
			suspend = false;
		}
	}

	class CleanException implements Runnable {
		@Override
		public void run() {
			logger.info(exceptionName + ": exceptionCount=" + exceptionCount.intValue()
					+ " clean up exception numbers! -----------------------");
			exceptionCount.set(0);
		}

	}

}
