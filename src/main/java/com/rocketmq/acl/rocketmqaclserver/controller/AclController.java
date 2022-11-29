
package com.rocketmq.acl.rocketmqaclserver.controller;


import com.google.common.base.Preconditions;
import com.rocketmq.acl.rocketmqaclserver.config.RMQConfigure;
import com.rocketmq.acl.rocketmqaclserver.model.JsonResult;
import com.rocketmq.acl.rocketmqaclserver.service.AclService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.AclConfig;
import org.apache.rocketmq.common.PlainAccessConfig;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/acl")
public class AclController {

    @Resource
    private AclService aclService;

    @Resource
    private RMQConfigure configure;

    @GetMapping("/enable.query")
    public Object isEnableAcl() {
        return new JsonResult<>(configure.isACLEnabled());
    }

    @GetMapping("/config.query")
    public AclConfig getAclConfig(HttpServletRequest request) {
        return aclService.getAclConfig(false);
    }

    @PostMapping("/add.do")
    public Object addAclConfig(@RequestBody PlainAccessConfig config) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(config.getAccessKey()), "accessKey is null");
        Preconditions.checkArgument(StringUtils.isNotEmpty(config.getSecretKey()), "secretKey is null");
        aclService.addAclConfig(config);
        return true;
    }

    @PostMapping("/delete.do")
    public Object deleteAclConfig(@RequestBody PlainAccessConfig config) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(config.getAccessKey()), "accessKey is null");
        aclService.deleteAclConfig(config);
        return true;
    }

    @PostMapping("/update.do")
    public Object updateAclConfig(@RequestBody PlainAccessConfig config) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(config.getSecretKey()), "secretKey is null");
        aclService.updateAclConfig(config);
        return true;
    }
}
