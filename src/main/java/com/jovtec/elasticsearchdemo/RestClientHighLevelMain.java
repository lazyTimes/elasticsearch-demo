package com.jovtec.elasticsearchdemo;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * 高级API 使用
 * @author zhaoxudong
 * @title: RestClientHighLevelMain
 * @projectName elasticsearch-demo
 * @description: 高级API
 * @date 2019/10/17 14:33
 */

public class RestClientHighLevelMain {
    public static void main(String[] args) {
        RestClientBuilder clientBuilder = RestClient.builder(
                new HttpHost("192.168.92.180", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
    }

    /**
     * 创建索引
     */
    private void createIndex(RestHighLevelClient client){
        // 1. 创建索引请求，并且指定索引名称
        CreateIndexRequest indexRequest = new CreateIndexRequest("twitter");
        // 2. 设置索引的 settings 注意和es原生方式不同需要加index.
        indexRequest.settings(Settings.builder()
            .put("index.number_of_shards", 3)
            .put("index.number_of_shards", 2));
        // 3. 设置mappings 映射。由于5.0升级需要加上 header请求类型
        indexRequest.mapping("tweet",
                "  {\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"text\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }",
                XContentType.JSON);
        // 4. 指定索引的别名
        indexRequest.alias(
                new Alias("twitter_alias")
        );
        // 可选参数提供
        // TODO: 这两个参数作用目前不太明确
        indexRequest.timeout(TimeValue.timeValueMinutes(2));
        indexRequest.timeout("2m");


    }


}
