package com.xjy.timevalue.mbg.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class News implements Serializable {
    /**
     * 主键id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 新闻标题
     *
     * @mbggenerated
     */
    private String title;

    /**
     * 作者
     *
     * @mbggenerated
     */
    private String author;

    /**
     * 文章发表时间
     *
     * @mbggenerated
     */
    @JsonFormat(pattern="yyyy年MM月dd日 HH时mm分ss秒",timezone = "GMT+8")
    private Date releaseTime;

    /**
     * 时效性（以天表示）
     *
     * @mbggenerated
     */
    private Double timeValue;

    /**
     * 说明
     *
     * @mbggenerated
     */
    private String information;

    /**
     * 新闻内容
     *
     * @mbggenerated
     */
    private String content;

    private static final long serialVersionUID = 1L;

    public News(){
        this.releaseTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        //自反性
        if (this == o) return true;
        //任何对象不等于null，比较是否为同一类型
        if (!(o instanceof News)) return false;
        //强制类型转换
        News news = (News) o;
        //比较属性值
        return getId().equals(news.getId());
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}