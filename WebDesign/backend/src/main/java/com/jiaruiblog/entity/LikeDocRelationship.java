package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class LikeDocRelationship {
    @Id
    private String id;

    private String userId;

    private String docId;

    private Date createDate;

    private Date updateDate;

}
