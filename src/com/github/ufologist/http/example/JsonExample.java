package com.github.ufologist.http.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonExample {
    public static void main(String[] args) {
        testFastJson();
        testFastJsonChinese();
        testOrgJson();
        testReadConsoleInput();
    }
    
    private static void testFastJson() {
        Map j = new HashMap();
        j.put("p2", "2");
        j.put("p1", "1");
        j.put("p3", "3");

        String jsonString = JSON.toJSONString(j, SerializerFeature.PrettyFormat);
        System.out.println(jsonString);
    }
    private static void testFastJsonChinese() {
        Map j = new HashMap();
        j.put("p2", "中文属性值test123");
        j.put("p1", "1");
        j.put("p3", "3");
        j.put("date", new Date());

        String jsonString = JSON.toJSONString(j, SerializerFeature.PrettyFormat, SerializerFeature.BrowserCompatible);
        System.out.println(jsonString);
    }

    private static void testOrgJson() {
        JSONObject j = new JSONObject();
        j.put("p2", "中文属性值test123");
        j.put("p1", "1");
        j.put("p3", "3");
        j.put("date", new Date());

        // 注意: 输出时属性的顺序与设置时是一样的
        System.out.println(j.toString(4));
    }
    
    private static void testReadConsoleInput() {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Enter username: ");
            String user = console.readLine();
            System.out.print("Enter password: ");
            String password = console.readLine();
            System.out.println(user + "@" + password);
            console.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                console.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
