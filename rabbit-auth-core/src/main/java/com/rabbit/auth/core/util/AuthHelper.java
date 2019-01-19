package com.rabbit.auth.core.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Hashtable;

/**
 * @ClassName AuthHelper
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 20:21
 **/
public class AuthHelper {

    public static void servletContextSetCode(ServletContext application, String code, Object object) {
        Hashtable ticketMap = servletContextCodeMap(application);
        ticketMap.put(code, object);
    }

    private static Hashtable<String, Object> servletContextCodeMap(ServletContext application) {
        Hashtable codeMap = (Hashtable) application.getAttribute("code");
        if (codeMap == null) {
            codeMap = new Hashtable();
            application.setAttribute("code", codeMap);
        }
        return codeMap;
    }

    private static Hashtable<String, Object> servletContextCodeMap(HttpSession httpSession) {
        ServletContext application = httpSession.getServletContext();
        return servletContextCodeMap(application);
    }
}
