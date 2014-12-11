package com.github.ufologist.http;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.github.ufologist.MidiPlayer;

public class HttpToolbox {
    public static Logger logger = Logger.getLogger(HttpToolbox.class);

    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_X_REQUESTED_WITH = "X-Requested-With";
    public static final String XMLHTTPREQUEST = "XMLHttpRequest";
    public static final String ACCEPT_ENCODING_GZIP = "gzip,deflate,sdch";

    public static final JsonResponseHandler jsonResponseHandler = new JsonResponseHandler();
    public static final GzipResponseHandler gzipResponseHandler = new GzipResponseHandler();

    private final String CONFIG_PROPERIES = "/config.properties";
    
    /**
     * 开启httpclient日志(控制台日志)
     * 
     * 如果要开启log4j的日志, 需要在log4j.properties中配置
     * log4j.logger.org.apache.http.wire=DEBUG
     * 而且不能同时开启这个日志
     * 
     * http://hc.apache.org/httpcomponents-client-ga/logging.html
     */
    public static void turnOnHttpWireLog() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        // 设置这个日志更详细, 一般不需要
        // System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    }

    private void loadConfig() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream(CONFIG_PROPERIES));
            String configUserName = properties.getProperty("userName");

            if (configUserName != null) {
                // 覆盖默认配置
                System.out.println(configUserName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 休息, 休息一下
     * 
     * @param min 毫秒
     * @param max 毫秒
     */
    public static void sleep(int min, int max) {
        try {
            long sleep = getRandom(min, max);
            System.out.println("\n休息..休息一下: " + sleep + " 马上回来.\n");
            Thread.sleep(sleep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * min-max(例如3000-10000)之间的随机数
     * 
     * @param min
     * @param max
     * @return min-max之间的随机数
     */
    public static long getRandom(int min, int max) {
        return (long) Math.floor(Math.random() * (max - min) + min);
    }

    public static void playSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MidiPlayer.getInstance().play(HttpToolbox.class.getResource("/win.mid"));
            }
        }).start();
    }
}
