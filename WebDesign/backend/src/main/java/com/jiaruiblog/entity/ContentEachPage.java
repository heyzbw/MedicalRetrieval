package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ContentEachPage {
    @Field(type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Integer)
    private int pageNum;
}
