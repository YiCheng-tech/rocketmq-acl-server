package com.rocketmq.acl.rocketmqaclserver.service;

import com.rocketmq.acl.rocketmqaclserver.model.AclRequest;
import org.apache.rocketmq.common.AclConfig;
import org.apache.rocketmq.common.PlainAccessConfig;

import java.util.List;

public interface AclService {

    AclConfig getAclConfig(boolean excludeSecretKey);

    void addAclConfig(PlainAccessConfig config);

    void deleteAclConfig(PlainAccessConfig config);

    void updateAclConfig(PlainAccessConfig config);

    void addOrUpdateAclTopicConfig(AclRequest request);

    void addOrUpdateAclGroupConfig(AclRequest request);

    void deletePermConfig(AclRequest request);

    void syncData(PlainAccessConfig config);

    void addWhiteList(List<String> whiteList);

    void deleteWhiteAddr(String addr);

    void synchronizeWhiteList(List<String> whiteList);
}
