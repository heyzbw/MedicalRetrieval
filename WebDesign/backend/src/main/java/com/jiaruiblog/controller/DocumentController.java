package com.jiaruiblog.controller;

import com.itextpdf.text.DocumentException;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.AdvanceDocumentDTO;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.dto.ImageDataDTO;
import com.jiaruiblog.entity.dto.RemoveObjectDTO;
import com.jiaruiblog.enums.FilterTypeEnum;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.service.IDocLogService;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.RedisService;
import com.jiaruiblog.service.impl.DocLogServiceImpl;
import com.jiaruiblog.service.impl.RedisServiceImpl;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.PermissionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @ClassName DocumentController
 * @Description 文档查询删除控制器
 * @Author luojiarui
 * @Date 2022/6/19 5:18 下午
 * @Version 1.0
 **/

@Api(tags = "文档模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/document")
public class DocumentController {

    @Resource
    IFileService iFileService;

    @Resource
    RedisService redisService;

    @Resource
    IDocLogService docLogService;


    @ApiOperation(value = "2.1 查询文档的分页列表页", notes = "根据参数查询文档列表")
    @PostMapping(value = "/list")
    public BaseApiResult list(@RequestBody DocumentDTO documentDTO)
            throws IOException {

//        long startTime = System.currentTimeMillis();

//        System.out.println("进入了查询方法");
        String userId = documentDTO.getUserId();
        System.out.println("userId:"+userId);
        if (StringUtils.hasText(documentDTO.getFilterWord()) &&
                documentDTO.getType() == FilterTypeEnum.FILTER) {
            String filterWord = documentDTO.getFilterWord();
            //非法敏感词汇判断
            SensitiveFilter filter = SensitiveFilter.getInstance();
            int n = filter.checkSensitiveWord(filterWord, 0, 1);
            //存在非法字符
            if (n > 0) {
                log.error("这个人输入了非法字符--> {},不知道他到底要查什么~", filterWord);
            } else {
                redisService.incrementScoreByUserId(filterWord, RedisServiceImpl.SEARCH_KEY);
                if (StringUtils.hasText(userId)) {
                    redisService.addSearchHistoryByUserId(userId, filterWord);
                }
            }
        }
//        System.out.println("用户类型为："+ documentDTO.getUserType());
//        如果我这里得到的UserType为一个String：ADMIN，我怎么得到一个PermissionEnum，并放入permission中？
        documentDTO.setPermission(PermissionEnum.valueOf(documentDTO.getUserType()));
//        PermissionEnum userPermission = PermissionUtil.getUserPermission(request);
//        documentDTO.setPermission(userPermission);
//        documentDTO.setUserId((String) request.getAttribute("id"));
//        System.out.println("");
        return iFileService.list(documentDTO);
    }



    @ApiOperation(value = "2.1 高级查询文档的分页列表页", notes = "根据参数查询文档列表")
    @PostMapping(value = "/listAdvance")
    public BaseApiResult listAdvance(@RequestBody AdvanceDocumentDTO documentDTO) throws IOException, ParseException {
        System.out.println("docunmentDTO:"+documentDTO);
        System.out.println("title"+documentDTO.getTitle());

//        long startTime = System.currentTimeMillis();

//        System.out.println("进入了查询方法");
        String userId = documentDTO.getUserId();
        if (StringUtils.hasText(documentDTO.getFilterWord()) &&
                documentDTO.getType() == FilterTypeEnum.FILTER) {
            String filterWord = documentDTO.getFilterWord();
            //非法敏感词汇判断
            SensitiveFilter filter = SensitiveFilter.getInstance();
            int n = filter.checkSensitiveWord(filterWord, 0, 1);
            //存在非法字符
            if (n > 0) {
                log.error("这个人输入了非法字符--> {},不知道他到底要查什么~", filterWord);
            } else {
                redisService.incrementScoreByUserId(filterWord, RedisServiceImpl.SEARCH_KEY);
                if (StringUtils.hasText(userId)) {
                    redisService.addSearchHistoryByUserId(userId, filterWord);
                }
            }
        }
        return iFileService.list_advance(documentDTO);
    }

    @ApiOperation(value = "2.2 查询文档的详细信息", notes = "查询文档的详细信息")
    @GetMapping(value = "/detail")
    public BaseApiResult detail(@RequestParam(value = "docId") String id,@RequestParam(value = "userId") String userId) throws IOException {
        System.out.println("username:"+userId);
        return iFileService.detail(id,userId);
    }

    @ApiOperation(value = "3.2 删除某个文档", notes = "删除某个文档")
    @DeleteMapping(value = "/auth/remove")
    public BaseApiResult remove(@RequestBody RemoveObjectDTO removeObjectDTO, HttpServletRequest request) {
        FileDocument fileDocument = iFileService.queryById(removeObjectDTO.getId());
        if (fileDocument == null){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        String username = (String) request.getAttribute("username");
        String userId = (String) request.getAttribute("id");
        User user = new User();
        user.setUsername(username);
        user.setId(userId);
        docLogService.addLog(user, fileDocument, DocLogServiceImpl.Action.DELETE);
        return iFileService.remove(removeObjectDTO.getId());
    }


    @ApiOperation(value = "2.3 指定分类时，查询文档的分页列表页", notes = "根据参数查询文档列表")
    @GetMapping(value = "/listWithCategory")
    public BaseApiResult listWithCategory(@ModelAttribute("documentDTO") DocumentDTO documentDTO,HttpServletRequest request) {

        String userId = (String) request.getAttribute("id");
        PermissionEnum userPermission = PermissionUtil.getUserPermission(request);
        documentDTO.setPermission(userPermission);
        documentDTO.setUserId(userId);

        FilterTypeEnum filterType = documentDTO.getType();
        if (filterType.equals(FilterTypeEnum.CATEGORY) || filterType.equals(FilterTypeEnum.TAG) ) {
            return iFileService.listWithCategory(documentDTO);
        } else {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
    }

    @GetMapping("/addKey")
    public BaseApiResult addKey(@RequestParam("key") String key) {

        final int ljr = redisService.addSearchHistoryByUserId("ljr", key);
        log.info(String.valueOf(ljr));
        redisService.incrementScoreByUserId(key, RedisServiceImpl.SEARCH_KEY);
        return BaseApiResult.success(key);
    }

    @GetMapping("/keyList")
    public BaseApiResult keyList() {
        List<String> keyList = redisService.getSearchHistoryByUserId("ljr");
        return BaseApiResult.success(keyList);
    }


    @GetMapping("/hot")
    public BaseApiResult hot() {
        List<String> keyList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        return BaseApiResult.success(keyList);
    }

    @ApiOperation(value = "2.1 高级查询文档的分页列表页", notes = "根据参数查询文档列表")
    @PostMapping("/Image")
    public BaseApiResult uploadImage(@RequestParam("filename") String filename, @RequestParam("imageList") MultipartFile[] imageList,HttpServletRequest request) throws IOException, DocumentException, AuthenticationException {
        String userId = (String) request.getAttribute("id");
        System.out.println("ImageMethod的UserId为:"+userId);
        iFileService.createScanPDF(filename,imageList,request);
        return null;
    }

//    @ApiOperation(value = "2.1 高级查询文档的分页列表页", notes = "根据参数查询文档列表")
//    @PostMapping("/Image")
//    public BaseApiResult uploadImage(@RequestBody ImageDataDTO imageDataDTO) {
//        String filename = imageDataDTO.getFilename();
//        MultipartFile[] imageList = imageDataDTO.getImageList();
//        // 进行后续处理
//        return null;
//    }

}
