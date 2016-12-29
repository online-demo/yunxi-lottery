package com.vteam.lucky.lottery.utils;

import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ChengLi
 * Date: 12-9-13
 * Time: 上午10:20
 * HTTP请求
 */
public class HttpUtil {
    /**
     * @param urlString  请求地址
     * @param method     POST OR GET
     * @param parameters 参数
     * @param propertys
     * @return
     * @throws java.io.IOException
     */
    public static String send(String urlString, String method,
                              Map<String, String> parameters, Map<String, String> propertys)
            throws IOException {
        HttpURLConnection urlConnection = null;

        String encode = parameters.get("encode");
        encode = StringUtils.isEmpty(encode) ? "utf-8" : encode;

        if (method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(URLEncoder.encode(parameters.get(key), encode));
                i++;
            }
            urlString += param;
        }
        String timeout = parameters.get("timeout");
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setReadTimeout(null == timeout ? 10000 : Integer.valueOf(timeout));

        if (propertys != null)
            for (String key : propertys.keySet()) {
                urlConnection.addRequestProperty(key, propertys.get(key));
            }

        if (method.equalsIgnoreCase("POST") && parameters != null) {
            StringBuffer param = new StringBuffer();
            for (String key : parameters.keySet()) {
                param.append("&");
                param.append(key).append("=").append(URLEncoder.encode(parameters.get(key), encode));
            }

            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }

        InputStream in = urlConnection.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int l = 0;
        while ((l = in.read(buffer)) > 0) {
            baos.write(buffer, 0, l);
        }
        baos.flush();
        in.close();
        baos.close();
        return new String(baos.toByteArray(), encode);
    }

    public static void main(String[] args) throws IOException {
        Map<String,String> params = new HashMap<>();
        params.put("action","send");
        params.put("userid","922");
        params.put("account","tyjr");
        params.put("password","vteam123");
        params.put("mobile","18621272477");
        params.put("content","hahahaha【天逸财金】");
        String url = "http://115.29.242.32:8888/sms.aspx";
        send(url,"POST",params,null);
    }
}