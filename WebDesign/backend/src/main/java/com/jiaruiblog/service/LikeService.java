package com.jiaruiblog.service;

import com.jiaruiblog.entity.LikeDocRelationship;

import java.io.IOException;

/**
 * @ClassName LikeService
 * @Description 用户对某个文档进行点赞或者收藏的操作
 * @Author luojiarui
 * @Date 2023/2/2 22:07
 * @Version 1.0
 **/
public interface LikeService {

    /**
     * 增加点赞，取消点赞；增加收藏，取消收藏
     * @param userId 用户id
     * @param entityType 实体类型：1：点赞；2：收藏
     * @param entityId 实体的id
     */
    void like(String userId, Integer entityType, String entityId) throws IOException;

    /**
     * 获取点赞的数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 返回点赞的数量
     */
    Long findEntityLikeCount(Integer entityType, String entityId);

    /**
     * 获取当前用户点赞的状态
     * @param userId 用户id
     * @param entityType 实体类型：1：点赞；2：收藏
     * @param entityId 实体的id
     * @return 返回该用户点赞的状态
     */
    int findEntityLikeStatus(String userId, Integer entityType, String entityId);

    LikeDocRelationship getExistLikeRelationship(String username, String docId);

    Long likeNum(String docId);
//    Long queryByDocId(String docId);
}
