package com.zs.elasticsearch.util;

import com.zs.elasticsearch.model.Status;
import org.apache.ibatis.mapping.ResultMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/19
 **/
public class ResultMapUtil {
    private static Map<String, Object> map = new HashMap<String, Object>();

    public static Map<String, Object> setResultMap(Status status) {
        map.put("code", status.getCode());
        map.put("msg", status.getMsg());
        return map;
    }
}
