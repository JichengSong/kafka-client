/*
 * Copyright 2013 Renren.com All right reserved. This software is the
 * confidential and proprietary information of Renren.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Renren.com.
 */
package com.renren.kafka.exception;

/**
 * Kafka异常类
 * 
 * @author yueqiang.zheng<tjuxiaoqiang@163.com>
 *
 */
public class InfraKafkaException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private final int responseCode;
    private final String errorMessage;

    public InfraKafkaException(Exception e) {
        super(e);
        this.responseCode = -1;
        this.errorMessage = "InfraKafkaException";
    }

    public InfraKafkaException(String message) {
        super(message);
        this.responseCode = -1;
        this.errorMessage = message;
    }

    public InfraKafkaException(String message, Throwable e) {
        super(message, e);
        this.responseCode = -1;
        this.errorMessage = message;
    }
    
    public int getResponseCode() {
        return responseCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

}
