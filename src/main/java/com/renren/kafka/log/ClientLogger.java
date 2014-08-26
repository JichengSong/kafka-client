package com.renren.kafka.log;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * @author yueqiang.zheng
 * @since 2014-2-7
 */
public class ClientLogger {
	private static Logger log;
	
	public static final String ClientLoggerName = "KafkaClientLogger";

    static {
        // 初始化Logger
        log = createLogger(ClientLoggerName);
    }

	private static Logger createLogger(String commonloggername) {
		String logConfigFilePath =
                System.getProperty("kafka.client.log.configFile",
                    System.getenv("KAFKA_CLIENT_LOG_CONFIGFILE"));
		
		Boolean isloadconfig =
                Boolean.parseBoolean(System.getProperty("kafka.client.log.loadconfig", "true"));
		
		final String log4j_resource_file =
                System.getProperty("kafka.client.log4j.resource.fileName", "log4j_kafka_client.xml");
		
		if (isloadconfig) {
			try {
				Class<?> DOMConfigurator = null;
	            Object DOMConfiguratorObj = null;
	            DOMConfigurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
	            DOMConfiguratorObj = DOMConfigurator.newInstance();
	            if (null == logConfigFilePath) {
	                // 如果应用没有配置，则使用jar包内置配置
	                Method configure = DOMConfiguratorObj.getClass().getMethod("configure", URL.class);
	                URL url = ClientLogger.class.getClassLoader().getResource(log4j_resource_file);
	                configure.invoke(DOMConfiguratorObj, url);
	            }
	            else {
	                Method configure = DOMConfiguratorObj.getClass().getMethod("configure", String.class);
	                configure.invoke(DOMConfiguratorObj, logConfigFilePath);
	            }
			} catch (Exception e) {
				System.err.println(e);
			}
			
		}
		
		return Logger.getLogger(ClientLoggerName);
	}
    
	
	public static Logger getLog() {
        return log;
    }


    public static void setLog(Logger log) {
        ClientLogger.log = log;
    }

}
