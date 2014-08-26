package com.renren.kafka.producer;

import java.util.Random;

import org.apache.log4j.Logger;

import kafka.producer.Partitioner;

public class DefaultPartition implements Partitioner<String> {

    private Random random = new Random();

    @Override
    public int partition(String key, int numPartitions) {
            return random.nextInt(numPartitions);
    }
}
