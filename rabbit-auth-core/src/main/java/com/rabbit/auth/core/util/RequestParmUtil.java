package com.rabbit.auth.core.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @ClassName RequestParmUtil
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 21:03
 **/
public class RequestParmUtil {

    public static String getParameterWithOut(HttpServletRequest request, String[] parms) {
        StringBuffer backUrl = request.getRequestURL();
        String params = "";
        Map<String, String []> parameterMap = request.getParameterMap();
        for (Map.Entry entry : parameterMap.entrySet()) {
            for (String parm : parms) {
                if (!parm.equals(entry.getKey())) {
                    if ("".equals(params))
                        params = (String) entry.getKey() + "=" + ((String[]) entry.getValue())[0];
                    else {
                        params = params + "&" + (String) entry.getKey() + "=" + ((String[]) entry.getValue())[0];
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(params)) {
            backUrl = backUrl.append("?").append(params);
        }
        return backUrl.toString();
    }
}
