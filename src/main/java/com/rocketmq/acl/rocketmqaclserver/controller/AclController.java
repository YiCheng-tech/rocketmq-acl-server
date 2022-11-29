
package com.rocketmq.acl.rocketmqaclserver.controller;


import com.google.common.base.Preconditions;
import com.rocketmq.acl.rocketmqaclserver.config.RMQConfigure;
import com.rocketmq.acl.rocketmqaclserver.model.JsonResult;
import com.rocketmq.acl.rocketmqaclserver.model.User;
import com.rocketmq.acl.rocketmqaclserver.model.UserInfo;
import com.rocketmq.acl.rocketmqaclserver.service.AclService;
import com.rocketmq.acl.rocketmqaclserver.util.WebUtil;
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
        if (!configure.isLoginRequired()) {
            return aclService.getAclConfig(false);
        }
        UserInfo userInfo = (UserInfo) WebUtil.getValueFromSession(request, WebUtil.USER_INFO);
        // if user info is null but reach here, must exclude secret key for safety.
        return aclService.getAclConfig(userInfo == null || userInfo.getUser().getType() != User.ADMIN);
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
