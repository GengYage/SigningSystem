package com.henu.android.utils;

import com.alibaba.fastjson.JSON;
import com.henu.android.entity.News;

public class JSONUtils {
    public static String messageToJson(News news){
        return JSON.toJSONString(news);
    }

    public static News JsonToMessage(String str){
        return JSON.parseObject(str, News.class);
    }
}