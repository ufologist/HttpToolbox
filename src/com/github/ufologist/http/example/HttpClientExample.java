package com.github.ufologist.http.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
 * [HttpClient Tutorial 重点章节](http://hc.apache.org/httpcomponents-client-ga/tutorial/html/index.html)
 * Preface
 * 1. Fundamentals
 * 2. Connection management
 * 3. HTTP state management
 * 5. Fluent API
 * 7.3. Using the FutureRequestExecutionService
 * -----------------------------
 * // 注意正确的关闭httpclient
 * // The underlying HTTP connection is still held by the response object
 * // to allow the response content to be streamed directly from the network socket.
 * // In order to ensure correct deallocation of system resources
 * // the user MUST call CloseableHttpResponse#close() from a finally clause.
 * // Please note that if response content is not fully consumed the underlying
 * // connection cannot be safely re-used and will be shut down and discarded
 * // by the connection manager.
 * CloseableHttpClient httpclient = HttpClients.createDefault();
 * try {
 *     HttpGet httpget = new HttpGet("http://localhost/");
 * 
 *     System.out.println("Executing request " + httpget.getRequestLine());
 *     CloseableHttpResponse response = httpclient.execute(httpget);
 *     try {
 *         System.out.println("----------------------------------------");
 *         System.out.println(response.getStatusLine());
 * 
 *         // Get hold of the response entity
 *         HttpEntity entity = response.getEntity();
 * 
 *         // If the response does not enclose an entity, there is no need
 *         // to bother about connection release
 *         if (entity != null) {
 *             InputStream instream = entity.getContent();
 *             try {
 *                 instream.read();
 *                 // do something useful with the response
 *             } catch (IOException ex) {
 *                 // In case of an IOException the connection will be released
 *                 // back to the connection manager automatically
 *                 throw ex;
 *             } finally {
 *                 // Closing the input stream will trigger connection release
 *                 instream.close();
 *             }
 *         }
 *     } finally {
 *         response.close();
 *     }
 * } finally {
 *     httpclient.close();
 * }
 * 
 * @author Sun
 * @version HttpUtil.java 2013-2-17 上午9:13:09
 * @see FluentExample
 */
public class HttpClientExample {
    private static Logger logger = Logger.getLogger(HttpClientExample.class);
    
    private CloseableHttpClient hc;
    private RequestConfig defaultRequestConfig;
    
    public static void main(String[] args) throws Exception {
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
        
        // 4.3版本的RequestBuilder已经非常好用了
        httpUtil.requestBuilderGet();
        httpUtil.requestBuilderPost();
    }

    public HttpClientExample(String userAgent) {
        // 4.3 以上都是通过 RequestConfig 来设置请求的配置项, 如timeout, CookieSpecs
        // can be set at the HTTP client and overridden on the HTTP request level if required.
        // HttpClients.custom().setDefaultRequestConfig(globalConfig)
        // httpGet.setConfig(requestConfig);
        
        // 3种timeout
        // A timeout value of zero is interpreted as an infinite timeout.
        // A negative value is interpreted as undefined (system default: -1).
        // 可以通过RequestConfig来设置, 默认为connectionRequestTimeout=0, connectTimeout=0, socketTimeout=0
        // 建立连接最终是通过 sock.connect(remoteAddress, connectTimeout); // PlainConnectionSocketFactory.connectSocket
        // 实际测试中, 即使 connectTimeout=0, 也会在20秒左右超时
        // connectionRequestTimeout 获取请求超时时间
        // the timeout in milliseconds used when requesting a connection from the connection manager
        // connectTimeout 连接超时时间
        // the timeout in milliseconds until a (Socket) connection is established
        // socketTimeout 数据传输超时
        // the timeout for waiting for data  or, put differently, a maximum period inactivity between two consecutive data packets
        defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();

        // 默认的HttpClient实例连接池只有2/20
        hc = HttpClients.custom().setUserAgent(userAgent)
                                 .setDefaultRequestConfig(defaultRequestConfig)
                                 .build();
    }

    private String requestBuilderGet() {
        HttpUriRequest request = RequestBuilder.get()
                                               .setUri("http://www.baidu.com")
                                               .addHeader("User-Agent", "A")
                                               .addHeader("X-A", "AAA")
                                               .addParameter("a", "1")
                                               .addParameter("b", "中文test123")
                                               .build();

        return executeRequest(request, true);
    }
    
    /**
     * XXX 通过RequestBuilder.post构造的post请求的formurlencoded编码有问题(使用了默认的ISO-8859-1, 应该采用UTF-8),
     * 这会造成无法对中文参数进行正确的编码, 请使用setEntity来避免这个问题, 稍微丧失了RequestBuilder一点点易用性
     * 
     * @return
     */
    private String requestBuilderPost() {
        HttpEntity entity = EntityBuilder.create()
                                         .setContentType(ContentType.APPLICATION_FORM_URLENCODED.withCharset(Consts.UTF_8))
                                         .setParameters(
                                              new BasicNameValuePair("a", "1"),
                                              new BasicNameValuePair("b", "中文test123")
                                          ).build();

        HttpUriRequest request = RequestBuilder.post()
                                               .setUri("http://www.baidu.com")
                                               .addHeader("User-Agent", "A")
                                               .addHeader("X-A", "AAA")
                                               // .addParameter("a", "1")
                                               // .addParameter("b", "中文test123")
                                               .setEntity(entity) // 使用setEntity来避免默认的URL编码问题
                                               .build();

        return executeRequest(request, true);
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
            responseBody = hc.execute(request, responseHandler);

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