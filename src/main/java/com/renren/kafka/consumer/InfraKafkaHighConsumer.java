/**
 * <p> @(#)InfraKafkaHighConsumer.java, 2013-9-22. </p>
 * 
 * Copyright 2013 RenRen, Inc. All rights reserved.
 */
package com.renren.kafka.consumer;

import java.util.*;

import com.renren.kafka.exception.InfraKafkaException;
import com.renren.kafka.exception.InfraKafkaExceptionMonitor;
import com.renren.kafka.log.ClientLogger;
import com.renren.kafka.util.InfraKafkaMessageUtils;
import com.renren.kafka.util.KafkaConfigLoader;
import com.renren.kafka.util.KafkaConstants;


import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;

import org.apache.log4j.Logger;


/**
 * @author yueqiang.zheng<tjuxiaoqiang@163.com>
 * @since 2014-2-7
 *
 */
public class InfraKafkaHighConsumer {
	private static final Logger logger = ClientLogger.getLog();

    private static InfraKafkaExceptionMonitor infraKafkaExceptionMonitor;
    
    static {
        infraKafkaExceptionMonitor = new InfraKafkaExceptionMonitor();
        infraKafkaExceptionMonitor.start(120, 60, 100,
                "Kafka consumer alarm");
    }

	private InternalConsumer internalConsumer;

    private final String topic;

    private final String group;

    private ConsumerIterator<Message> it;

    /**
     * count of consuming the messages
     */
	private int count = 0;

	public InfraKafkaHighConsumer(String topic, String group) throws InfraKafkaException {
        this.topic = topic;
        this.group = group;
        this.it = reconnect();
	}

	private ConsumerIterator<Message> reconnect() {
        if (logger.isInfoEnabled()) {
		    logger.info("consumer begins to connect");
        }

        if (internalConsumer != null) {
		    internalConsumer.close();
        }

        boolean success = false;
        ConsumerIterator<Message> it = null;
        while(!success) {
            internalConsumer = new InternalConsumer(topic, group);
            try {
                it = internalConsumer.initAndGetStreamIterator();
                success = true;
            } catch (InfraKafkaException e) {
                logger.error("consumer cannot connect to kafka, wait for a while and retry", e);
                monitorException(e);
                try {
                    Thread.sleep(KafkaConstants.CONSUMER_RECONNECT_INTERVAL_MS);
                } catch (InterruptedException e2) {
                    // do nothing
                }
            }
        }

        return it;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.renren.ad.infra.api.InfraKafkaConsumerApi#getMessage()
	 */
	public String getMessage()  {
        String result;

        while (true) {
            try {
                if (it.hasNext()) {
                    MessageAndMetadata<Message> msgAndMeta = it.next();
                    Message msg = msgAndMeta.message();
                    result = InfraKafkaMessageUtils.getMessage(msg);

                    // increase counting
                    count++;
                    if (count % 100000 == 0) {
                        logger.info("Thread id " + Thread.currentThread().getId()
                                + " has been consumed " + count);
                    }

                    // commit for every 1000 requests
                    // however, as default every 10sec the consumed messages are committed
                    if (count % 1000 == 0) {
                        commitOffset();
                    }

                    break;
                }
            } catch (Throwable e) {
                InfraKafkaException ike =
                        new InfraKafkaException("An Error occurs happens during fetching message from kafka," +
                                " trying to reconnect", e);
                logger.error(ike);
                monitorException(ike);
                it = reconnect();

                // then try again until the connection is OK
            }
        }

        return result;
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.renren.ad.infra.api.InfraKafkaConsumerApi#close()
	 */
	public void close() {
		internalConsumer.close();
	}

    private void monitorException(InfraKafkaException e) {
        monitorException(e, "");
    }

	private void monitorException(InfraKafkaException e, String alarmString) {
			infraKafkaExceptionMonitor.recordException(e, alarmString);
	}


	public boolean commitOffset() throws InfraKafkaException {
		internalConsumer.commitOffsets();
		return true;
	}

    /**
     * hold the real kafka consumer
     */
    class InternalConsumer {

        private final String topic;

        private final String group;

        private ConsumerConnector connector;

        private ConsumerIterator<Message> it;

        public InternalConsumer(String topic, String group) {
            this.topic = topic;
            this.group = group;
        }

        public ConsumerIterator<Message> initAndGetStreamIterator() throws InfraKafkaException {
            initKafkaConsumer(group);
            this.it = initKafkaStreams(topic);
            return it;
        }

        private void initKafkaConsumer(String group) throws InfraKafkaException {
            connector = kafka.consumer.Consumer
                    .createJavaConsumerConnector(makeConnectorConfig(group));
        }

        /**
         * Description: Initialize the kafka consumer streams from connector
         *
         * @throws InfraKafkaException
         */
        private ConsumerIterator<Message> initKafkaStreams(String topic) throws InfraKafkaException {
            Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
            topicCountMap.put(topic, 1); // we use only 1 stream per connector

            Map<String, List<KafkaStream<Message>>> consumerMap;
            try {
                consumerMap = connector.createMessageStreams(topicCountMap);
            } catch (kafka.common.InvalidMessageSizeException e) {
                throw new InfraKafkaException(
                        "the client has requested a range no longer available on the server!",
                        e);
            } catch (kafka.common.NoBrokersForPartitionException e) {
                throw new InfraKafkaException(
                        "No brokers exists, please restart brokers!", e);
            } catch (kafka.common.OffsetOutOfRangeException e) {
                throw new InfraKafkaException(
                        "The client has requested a range no longer available on the server:offset out of range!",
                        e);
            } catch (kafka.common.UnknownCodecException e) {
                throw new InfraKafkaException(
                        "The client has requested a range no longer available on the server:unknown code!",
                        e);
            } catch (kafka.common.UnknownException e) {
                throw new InfraKafkaException("UnknownException!", e);
            } catch (kafka.common.UnknownMagicByteException e) {
                throw new InfraKafkaException("The client has requested a range no longer available on the server:unknown magic byte!"
                        , e);
            } catch (kafka.consumer.ConsumerTimeoutException e) {
                throw new InfraKafkaException(
                        "Time out,need restart the broker!", e);
            } catch (Throwable e) {
                throw new InfraKafkaException("Unknown exception", e);
            }

            return consumerMap.get(topic).get(0).iterator();
        }

        /**
         * Description: load necessary configuration information from file
         *
         * @return the constructed config
         * @throws InfraKafkaException
         */
        private ConsumerConfig makeConnectorConfig(String group) throws InfraKafkaException {
            Properties props = KafkaConfigLoader.loadPropertyFile(this.getClass().getClassLoader().
                    getResourceAsStream(KafkaConstants.KAFKA_CONFIG_FILE));

            // define the groupid
            props.put("groupid", group);
            ConsumerConfig resultConfig;
            try {
                resultConfig = new ConsumerConfig(props);
                return resultConfig;

            } catch (kafka.common.InvalidConfigException e) {
                throw new InfraKafkaException("KafkaException:The given config parameter has invalid values!", e);
            } catch (kafka.common.UnavailableProducerException e) {
                throw new InfraKafkaException(
                        "KafkaException:The producer pool cannot be initialized !",
                        e);
            } catch (Exception e) {
                throw new InfraKafkaException(
                        "KafkaException:Other exception!", e);
            }
        }

        public void close() {
            connector.shutdown();
        }

        public void commitOffsets() {
            connector.commitOffsets();
        }
    }
}
