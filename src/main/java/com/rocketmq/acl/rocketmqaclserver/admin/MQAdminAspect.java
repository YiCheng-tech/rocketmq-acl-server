package com.rocketmq.acl.rocketmqaclserver.admin;

import com.rocketmq.acl.rocketmqaclserver.service.client.MQAdminInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.rocketmq.tools.admin.MQAdminExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Aspect
@Service
@Slf4j
public class MQAdminAspect {

    @Autowired
    private GenericObjectPool<MQAdminExt> mqAdminExtPool;

    public MQAdminAspect() {
    }

    @Pointcut("execution(* com.rocketmq.acl.rocketmqaclserver.service.client.MQAdminExtImpl..*(..))")
    public void mQAdminMethodPointCut() {

    }

    @Around(value = "mQAdminMethodPointCut()")
    public Object aroundMQAdminMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = null;
        try {
            MQAdminInstance.createMQAdmin(mqAdminExtPool);
            obj = joinPoint.proceed();
        } finally {
            MQAdminInstance.returnMQAdmin(mqAdminExtPool);
            log.debug("op=look method={} cost={}", joinPoint.getSignature().getName(), System.currentTimeMillis() - start);
        }
        return obj;
    }
}
