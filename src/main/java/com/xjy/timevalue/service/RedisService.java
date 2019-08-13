package com.xjy.timevalue.service;

import org.springframework.stereotype.Service;


public interface RedisService {
    void set(String key,String value);

    String get(String key);

    boolean expire(String key,long expire);

    void remove(String key);
}
