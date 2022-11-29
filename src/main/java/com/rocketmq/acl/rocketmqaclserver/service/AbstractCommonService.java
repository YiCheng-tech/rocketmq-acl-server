package com.rocketmq.acl.rocketmqaclserver.service;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.tools.admin.MQAdminExt;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class AbstractCommonService {
    @Resource
    protected MQAdminExt mqAdminExt;
    protected final Set<String> changeToBrokerNameSet(HashMap<String, Set<String>> clusterAddrTable,
        List<String> clusterNameList, List<String> brokerNameList) {
        Set<String> finalBrokerNameList = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(clusterNameList)) {
            try {
                for (String clusterName : clusterNameList) {
                    finalBrokerNameList.addAll(clusterAddrTable.get(clusterName));
                }
            }
            catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
        if (CollectionUtils.isNotEmpty(brokerNameList)) {
            finalBrokerNameList.addAll(brokerNameList);
        }
        return finalBrokerNameList;
    }
}
