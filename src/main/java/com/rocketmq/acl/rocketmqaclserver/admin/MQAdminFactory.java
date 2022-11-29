package com.rocketmq.acl.rocketmqaclserver.admin;

import com.rocketmq.acl.rocketmqaclserver.config.RMQConfigure;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.admin.MQAdminExt;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MQAdminFactory {
    private RMQConfigure rmqConfigure;

    public MQAdminFactory(RMQConfigure rmqConfigure) {
        this.rmqConfigure = rmqConfigure;
    }

    private final AtomicLong adminIndex = new AtomicLong(0);

    public MQAdminExt getInstance() throws Exception {
        RPCHook rpcHook = null;
        final String accessKey = rmqConfigure.getAccessKey();
        final String secretKey = rmqConfigure.getSecretKey();
        boolean isEnableAcl = StringUtils.isNotEmpty(accessKey) && StringUtils.isNotEmpty(secretKey);
        if (isEnableAcl) {
            rpcHook = new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
        }
        DefaultMQAdminExt mqAdminExt = null;
        if (rmqConfigure.getTimeoutMillis() == null) {
            mqAdminExt = new DefaultMQAdminExt(rpcHook);
        } else {
            mqAdminExt = new DefaultMQAdminExt(rpcHook, rmqConfigure.getTimeoutMillis());
        }
        mqAdminExt.setAdminExtGroup(mqAdminExt.getAdminExtGroup() + "_" + adminIndex.getAndIncrement());
        mqAdminExt.setVipChannelEnabled(Boolean.parseBoolean(rmqConfigure.getIsVIPChannel()));
        mqAdminExt.setUseTLS(rmqConfigure.isUseTLS());
        mqAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));
        mqAdminExt.start();
        log.info("create MQAdmin instance {} success.", mqAdminExt);
        return mqAdminExt;
    }
}
