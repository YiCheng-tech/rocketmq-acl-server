package com.rocketmq.acl.rocketmqaclserver.service.impl;

import com.google.common.base.Throwables;
import com.rocketmq.acl.rocketmqaclserver.service.AbstractCommonService;
import com.rocketmq.acl.rocketmqaclserver.service.AclService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.AclConfig;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.PlainAccessConfig;
import org.apache.rocketmq.common.protocol.body.ClusterInfo;
import org.apache.rocketmq.common.protocol.route.BrokerData;

import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AclServiceImpl extends AbstractCommonService implements AclService {

    @Override
    public AclConfig getAclConfig(boolean excludeSecretKey) {
        try {
            Optional<String> addr = getMasterSet().stream().findFirst();
            if (addr.isPresent()) {
                if (!excludeSecretKey) {
                    return mqAdminExt.examineBrokerClusterAclConfig(addr.get());
                } else {
                    AclConfig aclConfig = mqAdminExt.examineBrokerClusterAclConfig(addr.get());
                    if (CollectionUtils.isNotEmpty(aclConfig.getPlainAccessConfigs())) {
                        aclConfig.getPlainAccessConfigs().forEach(pac -> pac.setSecretKey(null));
                    }
                    return aclConfig;
                }
            }
        } catch (Exception e) {
            log.error("getAclConfig error.", e);
            throw Throwables.propagate(e);
        }
        AclConfig aclConfig = new AclConfig();
        aclConfig.setGlobalWhiteAddrs(Collections.emptyList());
        aclConfig.setPlainAccessConfigs(Collections.emptyList());
        return aclConfig;
    }

    @Override
    public void addAclConfig(PlainAccessConfig config) {
        try {
            //获取broker addr list
            Set<String> masterSet = getMasterSet();

            if (masterSet.isEmpty()) {
                throw new IllegalStateException("broker addr list is empty");
            }
            // check to see if account is exists
            for (String addr : masterSet) {
                AclConfig aclConfig = mqAdminExt.examineBrokerClusterAclConfig(addr);
                List<PlainAccessConfig> plainAccessConfigs = aclConfig.getPlainAccessConfigs();
                for (PlainAccessConfig pac : plainAccessConfigs) {
                    if (pac.getAccessKey().equals(config.getAccessKey())) {
                        throw new IllegalArgumentException(String.format("broker: %s, exist accessKey: %s", addr, config.getAccessKey()));
                    }
                }
            }

            // all broker
            for (String addr : getBrokerAddrs()) {
                mqAdminExt.createAndUpdatePlainAccessConfig(addr, config);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

    }

    @Override
    public void deleteAclConfig(PlainAccessConfig config) {
        try {
            for (String addr : getBrokerAddrs()) {
                log.info("Start to delete acl [{}] from broker [{}]", config.getAccessKey(), addr);
                if (isExistAccessKey(config.getAccessKey(), addr)) {
                    mqAdminExt.deletePlainAccessConfig(addr, config.getAccessKey());
                }
                log.info("Delete acl [{}] from broker [{}] complete", config.getAccessKey(), addr);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void updateAclConfig(PlainAccessConfig config) {
        try {
            for (String addr : getBrokerAddrs()) {
                AclConfig aclConfig = mqAdminExt.examineBrokerClusterAclConfig(addr);
                if (aclConfig.getPlainAccessConfigs() != null) {
                    PlainAccessConfig remoteConfig = null;
                    for (PlainAccessConfig pac : aclConfig.getPlainAccessConfigs()) {
                        if (pac.getAccessKey().equals(config.getAccessKey())) {
                            remoteConfig = pac;
                            break;
                        }
                    }
                    if (remoteConfig != null) {
                        remoteConfig.setSecretKey(config.getSecretKey());
                        remoteConfig.setAdmin(config.isAdmin());
                        config = remoteConfig;
                    }
                }
                mqAdminExt.createAndUpdatePlainAccessConfig(addr, config);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    private boolean isExistAccessKey(String accessKey,
        String addr) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        AclConfig aclConfig = mqAdminExt.examineBrokerClusterAclConfig(addr);
        List<PlainAccessConfig> plainAccessConfigs = aclConfig.getPlainAccessConfigs();
        if (plainAccessConfigs == null || plainAccessConfigs.isEmpty()) {
            return false;
        }
        for (PlainAccessConfig config : plainAccessConfigs) {
            if (accessKey.equals(config.getAccessKey())) {
                return true;
            }
        }
        return false;
    }

    private Set<BrokerData> getBrokerDataSet() throws InterruptedException, RemotingConnectException, RemotingTimeoutException, RemotingSendRequestException, MQBrokerException {
        ClusterInfo clusterInfo = mqAdminExt.examineBrokerClusterInfo();
        Map<String, BrokerData> brokerDataMap = clusterInfo.getBrokerAddrTable();
        return new HashSet<>(brokerDataMap.values());
    }

    private Set<String> getMasterSet() throws InterruptedException, MQBrokerException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        return getBrokerDataSet().stream().map(data -> data.getBrokerAddrs().get(MixAll.MASTER_ID)).collect(Collectors.toSet());
    }

    private Set<String> getBrokerAddrs() throws InterruptedException, MQBrokerException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        Set<String> brokerAddrs = new HashSet<>();
        getBrokerDataSet().forEach(data -> brokerAddrs.addAll(data.getBrokerAddrs().values()));
        return brokerAddrs;
    }
}
