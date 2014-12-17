package com.github.ufologist.http;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

/**
 * 处理gzip压缩的response, 也可以是plaintext, 支持指定编码格式
 * 
 * XXX 由于以前使用的 HttpClient 4.2.1 没有自动处理gzip压缩的response, 因此自己实现了这个.
 * 但是HttpClient 4.3.6(不知道具体是从哪个版本开始的)已经能自动处理gzip压缩的response(而且会自动添加请求头Accept-Encoding: gzip,deflate), 就不再需要这个了.
 * 
 * @author Sun
 * @version 2014-12-1
 */
public class GzipResponseHandler implements ResponseHandler<String> {
    private Charset charset;

    public GzipResponseHandler() {}
    public GzipResponseHandler(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        if (entity != null) {
            if (isGzip(entity)) { // 不是gzip的按照常规输出
                entity = new GzipDecompressingEntity(entity);
            }
    
            if (this.charset == null) { // 没有指定编码的尝试从content-type中取
                this.charset = ContentType.getOrDefault(entity).getCharset();
                if (this.charset == null) { // 取不到默认指定为UTF-8
                    this.charset = Consts.UTF_8;
                }
            }
    
            return EntityUtils.toString(entity, this.charset);
        }
        return null;
    }
    
    private boolean isGzip(HttpEntity entity) {
        Header contentEncoding = entity.getContentEncoding();
        boolean gzip = false;
        if (contentEncoding != null) {
            HeaderElement[] codecs = contentEncoding.getElements();
            for (int i = 0, length = codecs.length; i < length; i++) {
                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                    gzip = true;
                    break;
                }
            }
        }
        return gzip;
    }
}
