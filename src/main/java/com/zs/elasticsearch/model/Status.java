package com.zs.elasticsearch.model;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/19
 **/
public enum Status {
    SUCCESS(200,"操作成功"),
    FAILED(400,"操作失败"),
    ERROR(404,"索引已存在");

    private int code;
    private String msg;

    Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
