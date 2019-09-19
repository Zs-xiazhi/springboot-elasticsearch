package com.zs.elasticsearch.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/18
 **/
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfiguration {

    private ElasticsearchProperties properties;

    public ElasticsearchConfiguration(ElasticsearchProperties properties) {
        this.properties = properties;
    }

    @Bean("transportClient")
    public TransportClient createClient() {
        TransportClient transportClient = null;
        try {
            Settings settings = Settings.builder().put("cluster.name", properties.getClusterName())
                    .put("node.name", properties.getClusterNodes())
                    .put("client.transport.sniff", true)
                    .put("thread_pool.search.size", properties.getPool()).build();
            transportClient = new PreBuiltTransportClient(settings);
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(properties.getIp()), properties.getPort());
            transportClient.addTransportAddress(transportAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return transportClient;
    }
}
