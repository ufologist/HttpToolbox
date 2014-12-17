package com.github.ufologist.http;

import java.awt.Toolkit;
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

    private final String CONFIG_PROPERIES = "/config.properties";
    private static final String DEFAULT_MIDI_PATH = "/win.mid";
    
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

    /**
     * 播放一个midi声音文件
     * 
     * XXX 多次调用会造成JVM没有自动退出, 这是由于新起线程造成的, 如果不新起线程就会阻塞.
     * 如果介意JVM没有退出的, 还是建议使用 beep吧, 
     * 不过beep比开新线程还慢一点点, 连续5次调用, 新线程方式为141ms, beep为200多ms
     */
    public static void playSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MidiPlayer.getInstance().play(HttpToolbox.class.getResource(DEFAULT_MIDI_PATH));
            }
        }).start();
    }
    
    /**
     * 播放默认的响声, 可用于声音报警
     */
    public static void beep() {
        // http://www.rgagnon.com/javadetails/java-0001.html
        // C:\WINDOWS\Media\tada.wav
        // (Windows) The sound used is determined from the setting found in
        // Control Panel / Devices and Sounds/Sound Scheme/"Default Beep".
        // If no sound file is selected then the beep() will be a silence.
        // 控制面板 - 声音和设备 - 声音 - Windows 默认响声 对应的就是这个beep发出的声音
        Toolkit.getDefaultToolkit().beep();
    }
}
