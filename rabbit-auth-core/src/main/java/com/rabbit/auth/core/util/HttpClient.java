package com.rabbit.auth.core.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HttpClient
 * @Description TODO
 * @Author LZQ
 * @Date 2019/1/19 20:45
 **/
public class HttpClient {

    public static String postHttp(String url, JSONObject paramJson) {
        return postHttp(url, new StringEntity(paramJson.toString(), "UTF-8"));
    }

    public static String postHttp(String url, Map<String, String> paramMap) throws UnsupportedEncodingException {
        List params = new ArrayList();
        if (paramMap != null) {
            for (Map.Entry entry : paramMap.entrySet()) {
                params.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
        }
        return postHttp(url, new UrlEncodedFormEntity(params, "UTF-8"));
    }

    private static String postHttp(String url, HttpEntity formEntity) {
        CloseableHttpClient client = null;
        HttpPost post = null;
        String result = "";
        try {
            client = HttpClients.createDefault();

            post = new HttpPost(url);
            post.setEntity(formEntity);
            HttpResponse resp = client.execute(post);
            HttpEntity entity = resp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getHttp(String url) {
        CloseableHttpClient client = null;
        HttpGet get = null;
        String result = "";
        try {
            client = HttpClients.createDefault();
            get = new HttpGet(url);
            HttpResponse resp = client.execute(get);
            HttpEntity entity = resp.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
