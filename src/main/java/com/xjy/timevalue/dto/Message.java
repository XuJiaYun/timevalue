package com.xjy.timevalue.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
public class Message  implements Serializable {
    /**
     * 消息头meta 存放状态信息 code message
     */
    private Map<String,Object> meta = new HashMap<String,Object>();
    /**
     * 消息内容  存储实体交互数据
     */
    private Map<String,Object> data = new HashMap<String,Object>();

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Message setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Message setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
    public Message addMeta(String key, Object object) {
        this.meta.put(key,object);
        return this;
    }
    public Message addData(String key,Object object) {
        this.data.put(key,object);
        return this;
    }
    public Message ok(int statusCode,String statusMsg) {
        addMeta("success",Boolean.TRUE);
        addMeta("code",statusCode);
        addMeta("msg",statusMsg);
        addMeta("timestamp",new Timestamp(System.currentTimeMillis()));
        return this;
    }
    public Message error(int statusCode,String statusMsg) {
        addMeta("success",Boolean.FALSE);
        addMeta("code",statusCode);
        addMeta("msg",statusMsg);
        addMeta("timestamp",new Timestamp(System.currentTimeMillis()));
        return this;
    }
}
