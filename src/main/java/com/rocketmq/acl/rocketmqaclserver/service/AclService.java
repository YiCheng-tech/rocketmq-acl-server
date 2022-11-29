package com.rocketmq.acl.rocketmqaclserver.service;

import org.apache.rocketmq.common.AclConfig;
import org.apache.rocketmq.common.PlainAccessConfig;


public interface AclService {

    AclConfig getAclConfig(boolean excludeSecretKey);

    void addAclConfig(PlainAccessConfig config);

    void deleteAclConfig(PlainAccessConfig config);

    void updateAclConfig(PlainAccessConfig config);
}
