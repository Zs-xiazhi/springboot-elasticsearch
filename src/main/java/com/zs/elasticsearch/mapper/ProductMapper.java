package com.zs.elasticsearch.mapper;

import com.zs.elasticsearch.model.Product;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer pid);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer pid);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    @Select("select * from product")
    List<Product> getAllProduct();
}