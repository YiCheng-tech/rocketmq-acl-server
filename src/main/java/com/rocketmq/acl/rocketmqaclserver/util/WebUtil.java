
package com.rocketmq.acl.rocketmqaclserver.util;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class WebUtil {
    public static final String USER_INFO = "userInfo";


    public static Object getValueFromSession(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            return  session.getAttribute(key);
        }

        return null;
    }

}
