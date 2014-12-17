package com.github.ufologist.http.example;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * 异步 HTTP 请求
 * 
 * @author Sun
 * @version HttpAsyncClientExample.java 2014-12-17 下午17:17:21
 */
public class HttpAsyncClientExample {
    public static void main(String[] args) throws Exception {
        testAsync("http://localhost:8080/index.jsp", 200);
    }

    private static void testAsync(String url, int count) throws InterruptedException, ExecutionException {
        int connMaxTotal = count * 2;
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                                                              .setMaxConnPerRoute(connMaxTotal)
                                                              .setMaxConnTotal(connMaxTotal).build();
        try {
            // Start the client
            httpclient.start();

            HttpGet[] requests = new HttpGet[count];
            for (int i = 0; i < count; i++) {
                requests[i] = new HttpGet(url + "?_=" + i);
            }

            Queue<Future<HttpResponse>> queue = new LinkedList<Future<HttpResponse>>();
            // Execute requests asynchronously
            for (final HttpGet request : requests) {
                Future<HttpResponse> future = httpclient.execute(request, new FutureCallback<HttpResponse>() {
                    public void completed(final HttpResponse response2) {
                        System.out.println("->" + response2.getStatusLine());
                    }
                    public void failed(final Exception ex) {
                        System.out.println("->" + ex);
                    }
                    public void cancelled() {
                        System.out.println("->cancelled");
                    }
                });
                queue.add(future);
            }

            while (!queue.isEmpty()) {
                Future<HttpResponse> future = queue.remove();
                try {
                    future.get();
                } catch (ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
