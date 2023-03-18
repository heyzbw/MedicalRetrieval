package com.jiaruiblog.service;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.util.BaseApiResult;

import java.io.IOException;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface CollectService {

    /**
     * 新增文档收藏
     * @param collect -> Collect Doc Relationship
     * @return -> ApiResult
     */
    BaseApiResult insert(CollectDocRelationship collect) throws IOException;

    /**
     * 移除文档收藏
     * @param collect -> CollectDocRelationship
     * @return -> ApiResult
     */
    BaseApiResult remove(CollectDocRelationship collect);

    public BaseApiResult getDocByTagAndCateAndUserid(String cateId, String tagId, String keyword, Long pageNum, Long pageSize,String userId);
}
