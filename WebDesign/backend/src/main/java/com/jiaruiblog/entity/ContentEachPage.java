package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
public class ContentEachPage implements Serializable {
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Integer)
    private int pageNum;
}
