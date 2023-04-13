package com.jiaruiblog.controller;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.dto.CollectDTO;
import com.jiaruiblog.entity.dto.QueryDocByTagCateDTO;
import com.jiaruiblog.service.impl.CategoryServiceImpl;
import com.jiaruiblog.service.impl.CollectServiceImpl;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

/**
 * @ClassName CollectController
 * @Description user collection module
 * @Author luojiarui
 * @Date 2022/6/4 3:11 下午
 * @Version 1.0
 **/
@Api(tags = "用户收藏模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectServiceImpl collectServiceImpl;

//    @Autowired
//    CategoryServiceImpl categoryService;

    @ApiOperation(value = "2.3 新增一个收藏文档", notes = "新增单个收藏文档")
    @PostMapping(value = "/auth/insert")
    public BaseApiResult insert(@RequestParam("docId") String docId, HttpServletRequest request) throws IOException {
        System.out.println("收藏文档"+docId);
        System.out.println("用户id："+request.getAttribute("id"));
        CollectDTO collect = new CollectDTO();
        collect.setDocId(docId);
        return collectServiceImpl.insert(setRelationshipValue(collect, request));
    }

    @ApiOperation(value = "2.4 根据id移除某个收藏文档", notes = "根据id移除某个文档")
    @DeleteMapping(value = "/auth/remove")
    public BaseApiResult remove(@RequestBody CollectDTO collect, HttpServletRequest request) {
        return collectServiceImpl.remove(setRelationshipValue(collect, request));
    }

    @ApiOperation(value = "根据分类、标签查询", notes = "查询文档列表信息")
    @PostMapping(value = "/getDocByUserId")
//    public BaseApiResult getDocByTagCateKeyWord(@ModelAttribute("pageDTO") QueryDocByTagCateDTO pageDTO, HttpServletRequest request) {
    public BaseApiResult getDocByTagCateKeyWord(@RequestBody QueryDocByTagCateDTO pageDTO,
                                                HttpServletRequest request) {
        System.out.println("request:"+request);
        String userId = (String) request.getAttribute("id");
        System.out.println("userId_origin:"+userId);
        return collectServiceImpl.getDocByTagAndCateAndUserid(pageDTO.getCateId(), pageDTO.getTagId(), pageDTO.getKeyword(),
                Integer.toUnsignedLong(pageDTO.getPage() - 1), Integer.toUnsignedLong(pageDTO.getRows()),userId);
    }


    /**
     * @return com.jiaruiblog.entity.CollectDocRelationship
     * @Author luojiarui
     * @Description // 创建一个关系实体
     * @Date 9:36 下午 2022/6/23
     * @Param [collect, request]
     **/
    private CollectDocRelationship setRelationshipValue(CollectDTO collect, HttpServletRequest request) {
        CollectDocRelationship relationship = new CollectDocRelationship();
        relationship.setDocId(collect.getDocId());
        relationship.setUserId((String) request.getAttribute("id"));
        relationship.setCreateDate(new Date());
        relationship.setUpdateDate(new Date());
        return relationship;
    }

}
