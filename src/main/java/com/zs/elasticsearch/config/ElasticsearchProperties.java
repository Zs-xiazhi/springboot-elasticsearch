package com.zs.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/18
 **/
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticsearchProperties {
    private String ip;
    private Integer port;
    private String clusterName;
    private String clusterNodes;
    private Integer pool;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Integer getPool() {
        return pool;
    }

    public void setPool(Integer pool) {
        this.pool = pool;
    }
}
