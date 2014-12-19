# HTTP 工具箱
v0.1.0 2014-12-19

## 用途
* 列举了HttpClient的常用功能, 让我们可以快速上手完成发送HTTP请求的任务
* 并发请求
* 异步请求(HttpAsyncClient)
* 解析JSON
* 支持gzip
* 常用的log4j配置
* 常用的应用配置机制, 例如需要打包一个jar作为CLI程序, 那么如何保存/解析这些配置呢? 这里提供了默认的机制
* 定时调度任务

## 包含的第3方库
* [HttpClient 4.3.6](http://hc.apache.org)
  用于发送HTTP请求
* HttpAsyncClient 4.0.2
  用于发送异步HTTP请求
* [fastjson](https://github.com/alibaba/fastjson)
  用于将Object转成JSON
* [org.json](http://www.json.org/java/index.html)
  用于JSON格式化(修改了源码, 以满足可以按顺序输出字段(让其格式化输出的属性顺序和设置时的顺序保持一致 [JSONObject.toString() 中的各个字段按顺序输入](http://blog.csdn.net/choclover/article/details/6684954)), 还可以Make a prettyprinted JSON text of JSONObject(fastjson也有这个功能, 但是不理想, 输出的JSON是通过tab缩进的, 不能自定义缩进)
  
  如果想更多的定制格式化输出, 请修改org.json.JSONObject.write
* [cron4j](http://www.sauronsoftware.it/projects/cron4j/)
  用于做定时调度