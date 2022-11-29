package com.rocketmq.acl.rocketmqaclserver.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.tools.admin.MQAdminExt;

@Slf4j
public class MQAdminPooledObjectFactory implements PooledObjectFactory<MQAdminExt> {

    private MQAdminFactory mqAdminFactory;

    @Override
    public PooledObject<MQAdminExt> makeObject() throws Exception {
        DefaultPooledObject<MQAdminExt> pooledObject = new DefaultPooledObject<>(
            mqAdminFactory.getInstance());
        return pooledObject;
    }

    @Override
    public void destroyObject(PooledObject<MQAdminExt> p) {
        MQAdminExt mqAdmin = p.getObject();
        if (mqAdmin != null) {
            try {
                mqAdmin.shutdown();
            } catch (Exception e) {
                log.warn("MQAdminExt shutdown err", e);
            }
        }
        log.info("destroy object {}", p.getObject());
    }

    @Override
    public boolean validateObject(PooledObject<MQAdminExt> p) {
        MQAdminExt mqAdmin = p.getObject();
        ClusterInfo clusterInfo = null;
        try {
            clusterInfo = mqAdmin.examineBrokerClusterInfo();
        } catch (Exception e) {
            log.warn("validate object {} err", p.getObject(), e);
        }
        if (clusterInfo == null || MapUtils.isEmpty(clusterInfo.getBrokerAddrTable())) {
            log.warn("validateObject failed, clusterInfo = {}", clusterInfo);
            return false;
        }
        return true;
    }

    @Override
    public void activateObject(PooledObject<MQAdminExt> p) {
    }

    @Override
    public void passivateObject(PooledObject<MQAdminExt> p) {
    }

    public void setMqAdminFactory(MQAdminFactory mqAdminFactory) {
        this.mqAdminFactory = mqAdminFactory;
    }
}
