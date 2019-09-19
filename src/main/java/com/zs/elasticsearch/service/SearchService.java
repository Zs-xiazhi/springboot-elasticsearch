package com.zs.elasticsearch.service;

import com.zs.elasticsearch.mapper.ProductMapper;
import com.zs.elasticsearch.model.Product;
import com.zs.elasticsearch.model.Status;
import com.zs.elasticsearch.util.ElasticsearchUtil;
import com.zs.elasticsearch.util.ResultMapUtil;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/19
 **/
@Service
public class SearchService {

    @Autowired
    private ProductMapper productMapper;

    public Map<String,Object> addData() {
        Status status = Status.SUCCESS;
        List<Product> products = productMapper.getAllProduct();
        for (Product product : products) {
            Status status1 = ElasticsearchUtil.addDate(product, "test_index4", "test_type2", product.getPid().toString());
            if (status1.getCode() != 200) {
                status = status1;
            }
        }

        return ResultMapUtil.setResultMap(status);
    }

    public Map<String,Object> updateData() {
        Product product = new Product();
        product.setPid(50);
        product.setPname("裤子");
        Status status = ElasticsearchUtil.updateDate(product, "test_index4", "test_type2", "50");
        return ResultMapUtil.setResultMap(status);
    }

    public Map<String,Object> deleteDate() {
        Status status = ElasticsearchUtil.deleteById("test_index4", "test_type2", "50");
        return ResultMapUtil.setResultMap(status);
    }

    public Map<String, Object> searchDateById() {
        return ElasticsearchUtil.searchDataById("test_index4", "test_type2", "50", null);
    }

    public List<Map<String,Object>> searchDates() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchAllQueryBuilder allQuery = QueryBuilders.matchAllQuery();
        BoolQueryBuilder must = boolQueryBuilder.must(allQuery);
        return ElasticsearchUtil.searchListData("test_index4", "test_type2", must, 10, null, null, null);
    }

    public List<Map<String,Object>> searchLikeAll(String name) {
        MatchPhrasePrefixQueryBuilder query = QueryBuilders.matchPhrasePrefixQuery("pname", name);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder must = boolQueryBuilder.must(query);
        return ElasticsearchUtil.searchListData("test_index4", "test_type2", must, 10, null, null, "pname");
    }
}
