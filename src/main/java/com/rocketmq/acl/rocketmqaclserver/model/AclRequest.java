package com.rocketmq.acl.rocketmqaclserver.model;

import lombok.Data;
import org.apache.rocketmq.common.PlainAccessConfig;

@Data
public class AclRequest {

    private PlainAccessConfig config;

    private String topicPerm;

    private String groupPerm;
}
