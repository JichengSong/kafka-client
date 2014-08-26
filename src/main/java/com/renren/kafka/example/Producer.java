/**
 * <p> @(#)Producer.java, 2013-9-25. </p>
 * 
 * Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.kafka.example;

import org.apache.log4j.Logger;

import com.renren.kafka.exception.InfraKafkaException;
import com.renren.kafka.log.ClientLogger;
import com.renren.kafka.producer.InfraKafkaProducer;

/**
 * 
 * @author wmc
 * The producer demo
 */


public class Producer {
	//example
	private static Logger logger = ClientLogger.getLog();
	
	public static void main(String[] args) {
		String topic = "createMessage";
		InfraKafkaProducer producerHandler = null;
		
		producerHandler = InfraKafkaProducer.getInstance();
		
		try {
			int messageNo = 1;
			while (true) {
			    String messageString = "test-message"
						+ Integer.toString(messageNo);
			    messageNo++;

				if (producerHandler.sendMessage(topic, String.valueOf(String.valueOf(messageNo)), messageString)) {
					
                    logger.info("Successfully send messages "+ messageString);

				}
				
				try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
			}
		} catch (InfraKafkaException e) {
			e.printStackTrace();
		}
	}
}