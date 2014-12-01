package com.github.ufologist.http.example;

import java.net.URI;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONObject;

import com.github.ufologist.http.HttpToolbox;

/**
 * 使用httpclient提供的易用API
 * Easy to use facade API
 * 
 * 注意:
 * Fluent API默认是共用的一个HttpClient实例(Executor.CLIENT),
 * 使用了默认的(PoolingHttpClientConnectionManager)连接池,
 * MaxPerRoute: 100, MaxTotal: 200.
 * 具体请查看Request.execute
 * 
 * TODO 4.3以上Request不提供config方法了, 还不知道如何弥补这个空缺
 * 
 * @author Sun
 * @version FluentExample.java 2014-11-29 下午1:11:12
 * @see http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fluent.html
 */
public class FluentExample {
    public static void main(String[] args) throws Exception {
	HttpToolbox.turnOnHttpWireLog();

	// 使用URIBuilder来构造复杂的url
        URI uri = new URIBuilder().setScheme("http")
                    		  .setHost("cn.bing.com")
                    		  .setPath("/dict/")
                    		  .setParameter("a", "中文test123")
                    		  .setParameter("b", "")
                    		  .build();

//        testFluentGet(uri.toString());
//        testFluentPost("http://cn.bing.com/dict/");
//        testFluentWithContext();
//        testFluentJsonResponse();
        testFluentGzipResponse("http://libs.baidu.com/jquery/2.0.0/jquery.min.js");
    }

    private static void testFluentGet(String url) {
        try {
            String result = Request.Get(url)
        	    		   .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY) // 必须配置这个去掉cookie2 header
                                   .userAgent("Test")
                                   .addHeader(HttpHeaders.ACCEPT, "a") // HttpHeaders包含很多常用的http header
                                   .addHeader("AA", "BB")
                                   .execute().returnContent().asString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testFluentPost(String url) {
        try {
            String result = Request.Post(url)
                    		   .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY)
                    		   .bodyForm(Form.form().add("a", "abc123")
                    			   	        .add("b", "中文abc123").build(), Consts.UTF_8)
                                   // 或者传入自定义类型的body
                    		   // ContentType包含很多常用的content-type
                                   // .bodyString("Important stuff 中文abc123", ContentType.DEFAULT_TEXT)
                    		   .execute().returnContent().asString();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testFluentJsonResponse() {
        try {
            JSONObject result = Request.Get("http://api.ihackernews.com/page")
                    		       .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY)
                    		       .execute().handleResponse(HttpToolbox.jsonResponseHandler);
            System.out.println(result.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testFluentGzipResponse(String url) {
        try {
            String result = Request.Get(url)
        	    		   .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY)
                                   .addHeader(HttpHeaders.ACCEPT_ENCODING, HttpToolbox.ACCEPT_ENCODING_GZIP)
                                   .execute().handleResponse(HttpToolbox.gzipResponseHandler);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 由于Fluent API默认是共用的一个HttpClient实例, 因此HTTP的session状态本身就会被控制住.
     * 
     * 如果想获得更多的自定义选项, 可以使用Executor来控制.
     * 例如预先设置一个cookie, 保持多个请求的cookie是一致的, 这样服务器就能够识别出这些HTTP来自同一个用户
     * 
     * One can also use Executor directly in order to execute requests in a specific security context
     * whereby authentication details are cached and re-used for subsequent requests.
     * 
     * @see http://java.dzone.com/tips/fluency-and-control-httpclient
     */
    private static void testFluentWithContext() {
        // To maintain client-side state (cookies, authentication) between requests,
        // the fluent Executor helps by keeping a cookie store and setting up other types of authentication:
        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("a", "b");
        // 必须设置domain, 请求会根据访问的域名自动在请求header中添加属于该域名的cookie(浏览器默认行为)
        cookie.setDomain(".baidu.com");
        cookieStore.addCookie(cookie);

        // 预先设置请求中需要包含的cookie
        // 更多的自定义可以使用
        // HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(20).setMaxConnPerRoute(20);
        // Executor.newInstance(httpClient);
        Executor executor = Executor.newInstance()
    	    		            .cookieStore(cookieStore);
        
        try {
            // 发送2个一样的请求, 注意查看请求中cookie的情况
            Request request1 = Request.Get("http://www.baidu.com")
            	   		      .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            Request request2 = Request.Get("http://www.baidu.com")
	   		     	      .config(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

    	    String result1 = executor.execute(request1).returnContent().asString();
    	    System.out.println(result1);
    	    // 发送了第一个请求过后, executor会自动将response中的set-cookie补充的客户端的cookie中去(这就是一般浏览器的行为)
    	    String result2 = executor.execute(request2).returnContent().asString();
            System.out.println(result2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
