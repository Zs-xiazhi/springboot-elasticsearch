package com.zs.elasticsearch.util;

import com.zs.elasticsearch.model.Status;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/18
 **/
@Component
public class ElasticsearchUtil {

    @Autowired
    private TransportClient transportClient;

    private static TransportClient client;

    @PostConstruct
    public void init() {
        client = transportClient;
    }

    /**
     * @Company
     * @Author Zs
     *  判断索引是否存在
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    private static boolean isIndexExists(String index) {
        IndicesExistsResponse response = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        return response.isExists();
    }

    /**
     * @Company
     * @Author Zs
     *  创建索引
     * @Date Create in 2019/9/18
     * @param index :索引名
     * @return
     **/
    public static Status createIndex(String index) {
        if (!isIndexExists(index)) {
            CreateIndexResponse response = client.admin().indices().prepareCreate(index).execute().actionGet();
            if (response.isAcknowledged()) {
                return Status.SUCCESS;
            }
            return Status.FAILED;
        }
        return Status.ERROR;
    }

    /**
     * @Company
     * @Author Zs
     *  删除索引
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static Status deleteIndex(String index) {
        if (isIndexExists(index)) {
            DeleteIndexResponse response = client.admin().indices().prepareDelete(index).execute().actionGet();
            if (response.isAcknowledged()) {
                return Status.SUCCESS;
            }
            return Status.FAILED;
        }
        return Status.ERROR;
    }
    /**
     * @param
     * @return
     * @Company
     * @Author Zs
     * 判断指定type是否存在
     * @Date Create in 2019/9/19
     **/
    private static boolean isTypeExists(String index, String type) {
        return isIndexExists(index) ?
                client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists()
                : false;
    }

    /**
     * @Company
     * @Author Zs
     *  添加数据自定义索引
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static <T> Status addDate(T t,String index, String type, String id) {
        Map<String, Object> map = objectToMap(t);
        IndexResponse response = client.prepareIndex(index, type, id).setSource(map).get();
        return dealResponse(response);
    }

    /**
     * @Company
     * @Author Zs
     * 将对象转为map类型
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    private static <T> Map<String, Object> objectToMap(T t) {
        Map<String, Object> map = new HashMap<>(16);
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                 value= field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            map.put(name, value);
        }
        return map;
    }

    /**
     * @Company
     * @Author Zs
     *  添加数据自动生成索引
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static <T> Status addDate(T t, String index, String type) {
        return addDate(t, index, type, UUID.randomUUID().toString().replace("_", "").toUpperCase());
    }

    /**
     * @Company
     * @Author Zs
     *  通过id删除数据
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static <T> Status deleteById(String index, String type, String id) {
        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        return dealResponse(response);
    }

    public static <T> Status updateDate(T t, String index, String type, String id) {
        Map<String, Object> map = objectToMap(t);
        UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(map).execute().actionGet();
        return dealResponse(response);
    }

    /**
     * 根据响应状态处理返回结果
     * @param response
     * @return
     */
    private static Status dealResponse(DocWriteResponse response) {
        if (Status.SUCCESS.getCode() == response.status().getStatus()) {
            return Status.SUCCESS;
        }
        return Status.FAILED;
    }

    /**
     * @Company
     * @Author Zs
     *  通过id获取数据
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static Map<String, Object> searchDataById(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }
        GetResponse getResponse = getRequestBuilder.execute().actionGet();
        return getResponse.getSource();
    }

    /**
     * @Company
     * @Author Zs
     * 使用分词查询
     * @Date Create in 2019/9/19
     * @param
     * @return
     **/
    public static List<Map<String, Object>> searchListData(
            String index, String type, QueryBuilder query, Integer size,
            String fields, String sortField, String highlightField) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }

        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }

        searchRequestBuilder.setQuery(query);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits;
        long length = searchResponse.getHits().getHits().length;

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }
        return null;

    }

    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());

            if (StringUtils.isNotEmpty(highlightField)) {

                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();

                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
                    //遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }
        return sourceList;
    }



}
