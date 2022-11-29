package com.rocketmq.acl.rocketmqaclserver.admin;

import com.rocketmq.acl.rocketmqaclserver.config.RMQConfigure;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqAdminExtObjectPool {

    @Autowired
    private RMQConfigure rmqConfigure;

    @Bean
    public GenericObjectPool<MQAdminExt> mqAdminExtPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setTestWhileIdle(true);
        genericObjectPoolConfig.setMaxWaitMillis(10000);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(20000);
        MQAdminPooledObjectFactory mqAdminPooledObjectFactory = new MQAdminPooledObjectFactory();
        MQAdminFactory mqAdminFactory = new MQAdminFactory(rmqConfigure);
        mqAdminPooledObjectFactory.setMqAdminFactory(mqAdminFactory);
        GenericObjectPool<MQAdminExt> genericObjectPool = new GenericObjectPool<MQAdminExt>(
            mqAdminPooledObjectFactory,
            genericObjectPoolConfig);
        return genericObjectPool;
    }
}
