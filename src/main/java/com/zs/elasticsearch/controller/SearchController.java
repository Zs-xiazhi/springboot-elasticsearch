package com.zs.elasticsearch.controller;

import com.zs.elasticsearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/19
 **/
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @RequestMapping("/addData")
    public Map<String, Object> addData() {
        return searchService.addData();
    }

    @RequestMapping("/update")
    public Map<String, Object> updateData() {
        return searchService.updateData();
    }

    @RequestMapping("/delete")
    public Map<String, Object> delete() {
        return searchService.deleteDate();
    }

    @RequestMapping("/all")
    public List<Map<String, Object>> searchAll() {
        return searchService.searchDates();
    }

    @RequestMapping("/allLike")
    public List<Map<String,Object>> searchLike(String pname) {
        return searchService.searchLikeAll(pname);
    }

    @RequestMapping("/getById")
    public Map<String,Object> searchById() {
        return searchService.searchDateById();
    }
}
