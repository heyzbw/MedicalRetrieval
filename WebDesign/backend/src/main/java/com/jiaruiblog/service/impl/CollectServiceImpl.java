package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.FileDocumentDTO;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    private static final String COLLECTION_NAME = "collectCollection";
    private static final String COLLECTION_NAME_USER_ID = "userId";

    private static final String COLLECTION_NAME_DOC_ID = "docId";
    private static final String RELATE_COLLECTION_NAME = "relateCateCollection";
    private static final String ES_LIKE_NUM = "like_num";
    private static final String ES_CILCK_RATE = "click_rate";
    private static final String ES_COLLECT_NUM = "collect_num";

    private static final String DOC_ID = "docId";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    FileServiceImpl fileServiceImpl;

    @Autowired
    ElasticServiceImpl elasticServiceImpl;

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 对某个文档进行关注
     * @Date 9:31 下午 2022/6/23
     * @Param [collect]
     **/
    @Override
    public BaseApiResult insert(CollectDocRelationship collect) throws IOException {



        // 必须经过userId和docId的校验，否则不予关注
        if (!userServiceImpl.isExist(collect.getUserId()) || !fileServiceImpl.isExist(collect.getDocId())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
//        如果存在，则取消收藏
        CollectDocRelationship collectDb = getExistRelationship(collect);
        if (collectDb != null) {
            //        更新ES库中的文档
            boolean res = elasticServiceImpl.NumberOperation(collect.getDocId(),"MINI",ES_COLLECT_NUM);
//            elasticServiceImpl.RemoveCollect(collect.getDocId());
            return remove(collect);
//            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
//        更新ES库中的文档
        boolean res = elasticServiceImpl.NumberOperation(collect.getDocId(),"ADD",ES_COLLECT_NUM);
//        如果不存在，则进行收藏
        mongoTemplate.save(collect, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS_ADD_COLLECT);
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 删除收藏关系
     * @Date 9:43 下午 2022/6/23
     * @Param [collect]
     **/
    @Override
    public BaseApiResult remove(CollectDocRelationship collect) {
        collect = getExistRelationship(collect);
        while (collect != null) {
            mongoTemplate.remove(collect, COLLECTION_NAME);
            collect = getExistRelationship(collect);
        }
        return BaseApiResult.success(MessageConstant.SUCCESS_REMOVE_COLLECT);
    }

    /**
     * @return com.jiaruiblog.entity.CollectDocRelationship
     * @Author luojiarui
     * @Description // 查询已经存在的关系
     * @Date 9:37 下午 2022/6/23
     * @Param []
     **/
//    @Autowired
//    public CollectDocRelationship getExistRelationship(CollectDocRelationship collect) {
//        collect = Optional.ofNullable(collect).orElse(new CollectDocRelationship());
//
//        Query query = new Query()
//                .addCriteria(Criteria.where(DOC_ID).is(collect.getDocId())
//                        .and("userId").is(collect.getUserId()));
//
//        return mongoTemplate.findOne(
//                query, CollectDocRelationship.class, COLLECTION_NAME
//        );
//    }

    /**
     * @return java.lang.Long
     * @Author luojiarui
     * @Description 查询某个文档下面的点赞数量
     * @Date 22:35 2022/9/24
     * @Param [docId]
     **/
    public Long collectNum(String docId) {
        Query query = new Query().addCriteria(Criteria.where(DOC_ID).is(docId));
        return mongoTemplate.count(query, CollectDocRelationship.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的id删除掉点赞的关系
     * @Date 11:17 上午 2022/6/25
     * @Param [docId]
     **/
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where(DOC_ID).is(docId));
        List<CollectDocRelationship> relationships = mongoTemplate.find(query, CollectDocRelationship.class,
                COLLECTION_NAME);
        relationships.forEach(this::remove);
    }


//    根据用户id去搜索用户收藏的文档
    @Override
    public BaseApiResult getDocByTagAndCateAndUserid(String cateId, String tagId, String keyword, Long pageNum, Long pageSize,String userId) {
        System.out.println("userId:"+userId);
        Query queryCollect = new Query(Criteria.where(COLLECTION_NAME_USER_ID).is(userId));
//        queryCollect.fields().include(COLLECTION_NAME_DOC_ID);
        List<CollectDocRelationship> collectDocRelationships = mongoTemplate.find(queryCollect, CollectDocRelationship.class,COLLECTION_NAME);
        System.out.println("size:"+collectDocRelationships.size());




        Criteria criteria = new Criteria();
        if (StringUtils.hasText(cateId) && StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId).and("xyz.tagId").is(tagId);
        } else if (StringUtils.hasText(cateId) && !StringUtils.hasText(tagId)){
            criteria = Criteria.where("abc.categoryId").is(cateId);
        } else if (StringUtils.hasText(tagId) && !StringUtils.hasText(cateId)) {
            criteria = Criteria.where("xyz.tagId").is(tagId);
        }

        if (StringUtils.hasText(keyword)) {
            criteria.andOperator(Criteria.where("name").regex(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)));
        }

        // 查询审核完毕的文档
//        criteria.and("reviewing").is(false);

        Aggregation countAggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", "uploadDate", "thumbId")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", "fileId", "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", "fileId", "xyz"),
                Aggregation.match(criteria)
        );


        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", "uploadDate", "thumbId")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", "fileId", "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", "fileId", "xyz"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "uploadDate"),
                Aggregation.skip(pageNum*pageSize),
                Aggregation.limit(pageSize)
        );

        int count = mongoTemplate.aggregate(countAggregation, FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class)
                .getMappedResults().size();

        AggregationResults<FileDocumentDTO> aggregate = mongoTemplate.aggregate(aggregation,
                FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class);
        List<FileDocumentDTO> mappedResults = aggregate.getMappedResults();
        System.out.println("mappedResults:"+mappedResults);


        Map<String, Object> result = new HashMap<>(20);
        result.put("data", mappedResults);
        result.put("total", count);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return BaseApiResult.success(result);
    }

    @Override
    public CollectDocRelationship getExistRelationship(CollectDocRelationship collect) {
        collect = Optional.ofNullable(collect).orElse(new CollectDocRelationship());

        Query query = new Query()
                .addCriteria(Criteria.where(DOC_ID).is(collect.getDocId())
                        .and("userId").is(collect.getUserId()));

        return mongoTemplate.findOne(
                query, CollectDocRelationship.class, COLLECTION_NAME
        );
    }


}
