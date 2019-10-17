package com.jovtec.elasticsearchdemo;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Rest API main
 * @author zhaoxudong
 * @title: RestClientMain
 * @projectName elasticsearch-demo
 * @description: Rest API main
 * @date 2019/10/17 9:51
 */
public class RestClientMain {

    public static void main(String[] args) throws Exception {
        // 初始化
//        initCilent();
        // 请求和响应的几种方式
        requestAndresponse();
    }

    private static void requestAndresponse() throws IOException {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("192.168.92.180", 9200, "http"));
        RestClient restClient = restClientBuilder.build();
        // 1. 发送一个普通的get请求
//        Map<String, String> params = Collections.singletonMap("pretty", "true");
//        Response response = restClient.performRequest("GET", "/", params);

        // 2. 发送一个带参数的请求，并创建一个文档
        Map<Object, Object> singletonMap = Collections.emptyMap();
        // 请求url参数
        Map<String, String> params = Collections.singletonMap("pretty", "true");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        // 构建http请求体
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        // 创建一个文档
//        Response response = restClient.performRequest("PUT", "/posts/doc/1", params, entity);
        // 3. 创建一个_search 请求， 并且指定缓冲区的大小
        HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory heapBufferedResponseConsumerFactory
                = new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024);
        // 创建一个响应监听器
        ResponseListener responseListener = new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                System.err.println("响应成功");
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("响应失败");
            }
        };
//        restClient.performRequestAsync("GET", "/posts/_search", params, null, heapBufferedResponseConsumerFactory, responseListener);

        Response response = restClient.performRequest("PUT", "/posts/doc/1", params, entity);
        RequestLine requestLine = response.getRequestLine();
        HttpHost host = response.getHost();
        int statusCode = response.getStatusLine().getStatusCode();
        Header[] headers = response.getHeaders();
        String responseBody = EntityUtils.toString(response.getEntity());

        System.err.println(requestLine.getUri());
        System.err.println(responseBody);
    }

    private static void initCilent() {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost("192.168.92.180", 9200, "http"));
        // 设置最大超时时间
        restClientBuilder.setMaxRetryTimeoutMillis(10000);
        // 监听失效
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(HttpHost host) {
                System.err.println("连接失效");
            }
        });
        // 设置连接的回调内容
        restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
                System.err.println("sss");
                System.err.println("sss");
                System.err.println("sssf");
                // socket套接字连接超时时间
                return builder.setSocketTimeout(10000);
            }
        });
        // 设置回调的代理客户端
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setProxy(new HttpHost("proxy", 9000, "http"));
            }
        });

        RestClient restClient = restClientBuilder.build();
    }
}
