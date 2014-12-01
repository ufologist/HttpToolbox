package com.github.ufologist.http.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.log4j.Logger;

import com.github.ufologist.http.HttpToolbox;

/**
 * 常用的httpclient使用方法, 仅作示例, 一般不会直接使用, 建议用fluent api
 * 
 * 重要的对象
 * HttpClient
 * HttpGet/HttpPost...(HTTP request)
 * HttpResponse(HTTP response)
 * HttpEntity(HTTP entity)
 * -----------------------------
 * CloseableHttpClient httpclient = HttpClients.createDefault();
 * HttpGet httpget = new HttpGet("http://localhost/");
 * CloseableHttpResponse response = httpclient.execute(httpget);
 * try {
 *     HttpEntity entity = response.getEntity();
 *     if (entity != null) {
 *         long len = entity.getContentLength();
 *         if (len != -1 && len < 2048) {
 *             System.out.println(EntityUtils.toString(entity));
 *         } else {
 *             // Stream content out
 *         }
 *     }
 * } finally {
 *     response.close();
 * }
 * 
 * @author Sun
 * @version HttpUtil.java 2013-2-17 上午9:13:09
 * @see FluentExample
 */
public class HttpClientExample extends DefaultHttpClient {
    private static Logger logger = Logger.getLogger(HttpClientExample.class);
    
    public static void main(String[] args) {
	HttpToolbox.turnOnHttpWireLog();

	HttpClientExample httpUtil = new HttpClientExample("ABC");

	Map<String, String> headers = new HashMap<String, String>();
	// 也可以通过header设置User-Agent
	headers.put("User-Agent", "A");
	headers.put("X-A", "AAA");

	Map<String, String> formData = new HashMap<String, String>();
	formData.put("a", "123");
	formData.put("b", "中文abc123");

	httpUtil.get("http://www.baidu.com", headers, true);
	httpUtil.post("http://www.baidu.com", headers, formData, true);
    }

    public HttpClientExample(String userAgent) {
        HttpProtocolParams.setUserAgent(getParams(), userAgent);
        // 去掉cookie2 header
        HttpClientParams.setCookiePolicy(getParams(),
                CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public String get(String url, Map<String, String> headers, boolean systemOut) {
        HttpGet httpget = new HttpGet(url);
        setHeaders(httpget, headers);

        return executeRequest(httpget, systemOut);
    }

    public String post(String url, Map<String, String> headers,
            Map<String, String> formData, boolean systemOut) {
        HttpPost httppost = new HttpPost(url);
        setHeaders(httppost, headers);
        setFormData(httppost, formData);

        return executeRequest(httppost, systemOut);
    }

    private String executeRequest(HttpUriRequest request, boolean systemOut) {
        String responseBody = "";
        try {
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            responseBody = this.execute(request, responseHandler);

            if (systemOut) {
                System.out.println(request.getMethod() + " " + request.getURI());
                System.out.println("----------------------------------------");
                System.out.println(responseBody);
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    private void setHeaders(HttpMessage httpMessage,
            Map<String, String> headers) {
        if (headers == null) {
            return;
        }

        Set<Entry<String, String>> entrys = headers.entrySet();
        for (Entry<String, String> entry : entrys) {
            // 对于HttpClient中已经设置过的header(例如User-Agent), 会被新的header覆盖
            httpMessage.setHeader(entry.getKey(), entry.getValue());
        }
    }

    private void setFormData(HttpPost httppost, Map<String, String> formData) {
        if (formData == null) {
            return;
        }

        try {
            Set<Entry<String, String>> form = formData.entrySet();
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Entry<String, String> el : form) {
                nvps.add(new BasicNameValuePair(el.getKey(), el.getValue()));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}