package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.LikeDocRelationship;
import com.jiaruiblog.service.LikeService;
import com.jiaruiblog.util.BaseApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @ClassName LikeServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/2 22:07
 * @Version 1.0
 **/

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    RedisTemplate redisTemplate;

    // 对实体进行点赞的类型
    static final String entityLikeKeyFormat = "like:entity:{0}:{1}";
    private static final String INDEX_NAME = "likeCollection";

    private static final String DOC_ID = "docId";
    private static final String USER_ID = "userId";
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private FileServiceImpl fileServiceImpl;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void like(String userId, Integer entityType, String entityId){
        if (!userServiceImpl.isExist(userId) || !fileServiceImpl.isExist(entityId)) {
            return ;
        }

        LikeDocRelationship likeDb = getExistLikeRelationship(userId,entityId);
        if(likeDb != null){
            remove(userId,entityId);
        }

        likeDb = new LikeDocRelationship();
        likeDb.setUserId(userId);
        likeDb.setDocId(entityId);
        likeDb.setCreateDate(new Date());
        likeDb.setUpdateDate(new Date());

        mongoTemplate.save(likeDb,INDEX_NAME);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                // 构建被点赞的实体对应redis的k
//                String entityLikeKey = "like:entity:entityType:entityId";
                String entityLikeKey = MessageFormat.format(entityLikeKeyFormat, entityType, entityId);
                // 构建被点赞的实体对应的作者再redis中的key,用于统计后期某用户总共收获了多少个赞，社交论坛（例如虎扑）就有这个功能
//                String userLikeKey = "like:user:entityUserId";
                // 判断集合中是否有userId这个值
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务
                operations.multi();

                if (isMember){
                    // 移除userId这个值
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //减一（方便统计用户后期获得赞的总数）
//                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    //加一（方便统计用户后期获得赞的总数）
//                    operations.opsForValue().increment(userLikeKey);
                }
                // 提交事务
                return operations.exec();
            }
        });
    }


//     LikeDocRelationship getExistLikeRelationship(String username,String docId){
//        Query query = new Query()
//                .addCriteria(Criteria.where(DOC_ID).is(docId)
//                        .and("userId").is(username));
//
//        return mongoTemplate.findOne(
//          query,LikeDocRelationship.class,INDEX_NAME
//        );
//    }

    private void remove(String userId,String docId){
        LikeDocRelationship liekDb = getExistLikeRelationship(userId,docId);
        while (liekDb != null){
            mongoTemplate.remove(liekDb,INDEX_NAME);
        }
    }



    // 查询某实体点赞的数量
    @Override
    public Long findEntityLikeCount(Integer entityType, String entityId) {
//        String entityLikeKey = "like:entity:entityType:entityId";
        String entityLikeKey = MessageFormat.format(entityLikeKeyFormat, entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // //查询某人对某实体的点赞状态
    @Override
    public int findEntityLikeStatus(String userId, Integer entityType, String entityId) {
//        String entityLikeKey = "like:entity:entityType:entityId";
        String entityLikeKey = MessageFormat.format(entityLikeKeyFormat, entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    @Override
    public LikeDocRelationship getExistLikeRelationship(String username, String docId) {
        Query query = new Query()
                .addCriteria(Criteria.where(DOC_ID).is(docId)
                        .and("userId").is(username));

        return mongoTemplate.findOne(
          query,LikeDocRelationship.class,INDEX_NAME
        );
    }


}
