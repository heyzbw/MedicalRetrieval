package com.jiaruiblog.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.collect.Maps;
import com.itextpdf.text.DocumentException;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.*;
import com.jiaruiblog.entity.data.ThumbIdAndDate;
import com.jiaruiblog.entity.dto.AdvanceDocumentDTO;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.ocrResult.*;
import com.jiaruiblog.entity.vo.DocWithCateVO;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.service.*;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.CallFlask;
import com.jiaruiblog.util.PdfUtil;
import com.jiaruiblog.util.QuantileNormalization;
import com.jiaruiblog.util.converter.ImageToPdfConverter;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.http.auth.AuthenticationException;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author jiarui.luo
 */
@Slf4j
@Service
public class FileServiceImpl implements IFileService {

    public static final String COLLECTION_NAME = "fileDatas";
    public static final String OCR_RESULT_NAME = "ocr_result";

    private static final String PDF_SUFFIX = ".pdf";

    private static final String FILE_NAME = "filename";

    private static final String UPLOAD_DATE_FILED_NAME = "uploadDate";

    private static final String THUMBID_FILED_NAME = "thumbId";
    //    uploadDate
    private static final String CONTENT = "content";

    private static final String[] EXCLUDE_FIELD = new String[]{"md5", "content", "contentType", "suffix", "description",
            "gridfsId", "thumbId", "textFileId", "errorMsg"};

    @Resource
    SystemConfig systemConfig;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Resource
    private GridFSBucket gridFsBucket;

    @Resource
    private CategoryServiceImpl categoryServiceImpl;

    @Resource
    private CommentServiceImpl commentServiceImpl;

    @Resource
    private CollectServiceImpl collectServiceImpl;

    @Resource
    private TagServiceImpl tagServiceImpl;

    @Resource
    private ElasticServiceImpl elasticServiceImpl;

    @Resource
    private RedisService redisService;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Resource
    private IUserService userService;

    @Resource
    private File2OcrService file2OcrService;

    @Resource
    private LikeService likeService;



    /**
     * js文件流上传附件
     *
     * @param fileDocument 文档对象
     * @param inputStream  文档文件流
     * @return FileDocument
     */
    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        //已存在该文件，则实现秒传
        FileDocument dbFile = getByMd5(fileDocument.getMd5());
        if (dbFile != null) {
            return dbFile;
        }
        //GridFSInputFile inputFile = gridFsTemplate

        String gridfsId = uploadFileToGridFs(inputStream, fileDocument.getContentType());
        fileDocument.setGridfsId(gridfsId);

        fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);

        // TODO 在这里进行异步操作

        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update();
        update.set("textFileId", fileDocument.getTextFileId());
        update.set("thumbId", fileDocument.getThumbId());
        update.set("previewFileId", fileDocument.getPreviewFileId());
        update.set("description", fileDocument.getDescription());
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

    }

    /**
     * @Author luojiarui
     * @Description // 更新文档状态
     * @Date 15:41 2022/11/13
     * @Param [fileDocument, state]
     **/
    @Override
    public void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) throws TaskRunException {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        if (state != DocStateEnum.FAIL) {
            errorMsg = "无";
        }
        Update update = new Update();
        update.set("docState", state);
        update.set("errorMsg", errorMsg);
        try {
            mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("更新文档状态信息{}==>出错==>{}", fileDocument, e);
            throw new TaskRunException("更新文档状态信息==>出错==>{}", e);
        }
    }

    /**
     * @Author luojiarui
     * @Description // 从gridFs中删除文件
     * @Date 18:01 2022/11/13
     * @Param [id]
     **/
    @Override
    public void deleteGridFs(String... id) {
        Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).in(id));
        gridFsTemplate.delete(deleteQuery);
    }

    /**
     * 表单上传附件
     *
     * @param md5  文件md5
     * @param file 文件
     * @return FileDocument
     */
    @Override
    public FileDocument saveFile(String md5, MultipartFile file) {
        //已存在该文件，则实现秒传
        FileDocument fileDocument = getByMd5(md5);
        if (fileDocument != null) {
            return fileDocument;
        }
        String originFilename = file.getOriginalFilename();
        fileDocument = new FileDocument();
        fileDocument.setName(originFilename);
        fileDocument.setSize(file.getSize());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setUploadDate(new Date());
        fileDocument.setMd5(md5);

        if (StringUtils.hasText(originFilename)) {
            String suffix = originFilename.substring(originFilename.lastIndexOf("."));
            fileDocument.setSuffix(suffix);
        }

        try {
            String gridfsId = uploadFileToGridFs(file.getInputStream(), file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // 异步保存数据标签
//        tagServiceImpl.saveTagWhenSaveDoc(fileDocument);

        return fileDocument;
    }

    @Override
    public void saveTagWhenSaveDoc(FileDocument fileDocument, List<String> tags){
        tagServiceImpl.saveTagWhenSaveDoc(fileDocument,tags);
    }
    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 使用用户id 和 用户名进行保存，此接口必须使用auth进行验证
     * @Date 12:18 2023/2/19
     * @Param [file, userId, username]
     **/
    @Override
    public BaseApiResult documentUpload(MultipartFile file, String userId, String username) throws AuthenticationException {
        User user = userService.queryById(userId);
        if (user == null) {
            throw new AuthenticationException();
        }
        // 用户非管理员且普通用户禁止
//        if (Boolean.TRUE.equals(!systemConfig.getUserUpload()) && user.getPermissionEnum() != PermissionEnum.ADMIN) {
//            throw new AuthenticationException();
//        }
        List<String> availableSuffixList = com.google.common.collect.Lists
                .newArrayList("pdf", "png", "docx", "pptx", "xlsx", "html", "md", "txt");
        try {
            if (file != null && !file.isEmpty()) {
                String originFileName = file.getOriginalFilename();
                if (!StringUtils.hasText(originFileName)) {
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.FORMAT_ERROR);
                }
                //获取文件后缀名
                String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                if (!availableSuffixList.contains(suffix)) {
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.FORMAT_ERROR);
                }
                String fileMd5 = SecureUtil.md5(file.getInputStream());

                //已存在该文件，则拒绝保存
                FileDocument fileDocumentInDb = getByMd5(fileMd5);
                if (fileDocumentInDb != null) {
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_DUPLICATE);
                }
                FileDocument fileDocument = saveToDb(fileMd5, file, userId, username);

//                saveTagWhenSaveDoc(fileDocument,);

                //  取ocr结果
//                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
//                    try {
//                        List<OcrResultNew> ocrResultList = file2OcrService.getOcrByPY(fileMd5);
//                        fileDocument.setOcrResultList(ocrResultList);
//                        System.out.println("documentUpload：处理完了OCR");
//
//                        switch (suffix) {
//                            case "pdf":
//                            case "docx":
//                            case "pptx":
//                            case "xlsx":
//                            case "html":
//                            case "md":
//                            case "txt":
//                                taskExecuteService.execute(fileDocument);
//                                break;
//                            default:
//                                break;
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                });

//                callFlask.doUpload(fileMd5);

                // 目前支持这一类数据进行预览

                return BaseApiResult.success(fileDocument.getId());
            } else {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    public ResponseModel documentUpload_scan(MultipartFile file) throws IOException {
//        String fileSavePath = "C:\\Users\\22533\\Desktop\\testPaper\\scan"+file.getOriginalFilename();

        String directoryPath = "C:\\Users\\22533\\Desktop\\testPaper\\scan\\";
        byte[] bytes = file.getBytes();
        Path path = Paths.get(directoryPath,file.getOriginalFilename());
        Files.write(path,bytes);

        return null;
    }

    @Override
    public ResponseModel documentUpload_noAuth_multi(MultiFilesUploadObj multiFilesUploadObj) throws AuthenticationException {

        String userId = multiFilesUploadObj.getUserId();
        String username = multiFilesUploadObj.getUsername();
        MultipartFile[] files = multiFilesUploadObj.getFiles();

        List<String> availableSuffixList = com.google.common.collect.Lists.newArrayList("pdf", "png", "docx", "pptx", "xlsx");
        ResponseModel model = ResponseModel.getInstance();
        List<String> listFileId = new ArrayList<>();

        for(int i=0;i<files.length;i++)
        {
            MultipartFile file = files[i];
            try {
                if (file != null && !file.isEmpty()) {
                    String originFileName = file.getOriginalFilename();
                    if (!StringUtils.hasText(originFileName)) {
                        model.setMessage(originFileName+"的格式不支持！");
                        return model;
                    }
                    //获取文件后缀名
                    String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                    if (!availableSuffixList.contains(suffix)) {
                        model.setMessage(originFileName+"的格式不支持！");
                        return model;
                    }
                    String fileMd5 = SecureUtil.md5(file.getInputStream());
                    FileDocument fileDocument = saveToDb(fileMd5,file,userId,username);
//                    FileDocument fileDocument = saveFile(fileMd5, file);

                    //获取OCR识别结果
                    List<OcrResultNew> ocrResultNewList = file2OcrService.getOcrByPY(fileMd5);
                    fileDocument.setOcrResultNewList(ocrResultNewList);
//                fileDocument.se

                    switch (suffix) {
                        case "pdf":
                        case "docx":
                        case "pptx":
                        case "xlsx":
                            taskExecuteService.execute(fileDocument);
                            break;
                        default:
                            break;
                    }
                    listFileId.add(fileDocument.getId());
//                    model.setData(fileDocument.getId());

                } else {
                    model.setMessage("请传入文件");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                model.setMessage(ex.getMessage());
            }
        }
        model.setData("");
        model.setCode(ResponseModel.SUCCESS);
        model.setMessage("上传成功");
        return model;
    }

    @Override
    public ResponseModel documentUpload_noAuth(UploadFileObj uploadFileObj) throws AuthenticationException {

        System.out.println("我要保存文件");
        MultipartFile file = uploadFileObj.getFile();
        List<String> labels = uploadFileObj.getLabels();
        String userId = uploadFileObj.getUserId();
        String username = uploadFileObj.getUsername();
        List<String> availableSuffixList = com.google.common.collect.Lists.newArrayList("pdf", "png", "docx", "pptx", "xlsx");
        ResponseModel model = ResponseModel.getInstance();
        try {
            if (file != null && !file.isEmpty()) {
                String originFileName = file.getOriginalFilename();
                if (!StringUtils.hasText(originFileName)) {
                    model.setMessage("格式不支持！");
                    return model;
                }
                //获取文件后缀名
                String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
                if (!availableSuffixList.contains(suffix)) {
                    model.setMessage("格式不支持！");
                    return model;
                }
                String fileMd5 = SecureUtil.md5(file.getInputStream());

                FileDocument fileDocumentInDb = getByMd5(fileMd5);
                boolean flag_exist = (fileDocumentInDb != null);

                FileDocument fileDocument = saveToDb(fileMd5, file,userId,username);

                if(!flag_exist){
                    System.out.println("不存在，那就先获取ocr吧");
//                FileDocument fileDocument = saveFile(fileMd5, file);
                    saveTagWhenSaveDoc(fileDocument,labels);
                    //获取OCR识别结果
                    List<OcrResultNew> ocrResultNewList = file2OcrService.getOcrByPY(fileMd5);
                    fileDocument.setOcrResultNewList(ocrResultNewList);

                    switch (suffix) {
                        case "pdf":
                        case "docx":
                        case "pptx":
                        case "xlsx":
                            taskExecuteService.execute(fileDocument);
                            break;
                        default:
                            break;
                    }
                }
                model.setData(fileDocument.getId());
                model.setCode(ResponseModel.SUCCESS);
                model.setMessage("上传成功");
            } else {
                model.setMessage("请传入文件");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            model.setMessage(ex.getMessage());
        }
        return model;
    }

    /**
     * @return java.lang.String
     * @Author luojiarui
     * @Description 存入数据库及解析索引
     * @Date 12:12 2023/2/19
     * @Param [fileMd5, file]
     **/
    private FileDocument saveToDb(String md5, MultipartFile file, String userId, String username) {

        FileDocument fileDocument = getByMd5(md5);
        if (fileDocument != null) {
            return fileDocument;
        }

        String originFilename = file.getOriginalFilename();
        fileDocument = new FileDocument();
        fileDocument.setName(originFilename);
        fileDocument.setSize(file.getSize());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setUploadDate(new Date());
        fileDocument.setMd5(md5);
        fileDocument.setUserId(userId);
        fileDocument.setUserName(username);

        if (StringUtils.hasText(originFilename)) {
            String suffix = originFilename.substring(originFilename.lastIndexOf("."));
            fileDocument.setSuffix(suffix);
        }

        try {
            String gridfsId = uploadFileToGridFs(file.getInputStream(), file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // 异步保存数据标签
        tagServiceImpl.saveTagWhenSaveDoc(fileDocument);

        return fileDocument;

    }

    /**
     * 上传文件到Mongodb的GridFs中
     *
     * @param in          -> InputStream
     * @param contentType -> String
     * @return -> String
     */
    private String uploadFileToGridFs(InputStream in, String contentType) {
        String gridfsId = IdUtil.simpleUUID();
        //文件，存储在GridFS中
        gridFsTemplate.store(in, gridfsId, contentType);
        // 其实应该使用文件id进行存储
//        ObjectId objectId = gridFsTemplate.store()
        return gridfsId;
    }

    /**
     * 上传文件到Mongodb的GridFs中
     *
     * @param in
     * @param contentType
     * @return
     */
    @Override
    public String uploadFileToGridFs(String prefix, InputStream in, String contentType) {
        String gridfsId = prefix + IdUtil.simpleUUID();
        //文件，存储在GridFS中
        gridFsTemplate.store(in, gridfsId, contentType);
        return gridfsId;
    }

    /**
     * 删除附件
     *
     * @param id           文件id
     * @param isDeleteFile 是否删除文件
     */
    @Override
    public void removeFile(String id, boolean isDeleteFile) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, COLLECTION_NAME);
        if (fileDocument != null) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, COLLECTION_NAME);
            if (isDeleteFile) {
                Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(fileDocument.getGridfsId()));
                gridFsTemplate.delete(deleteQuery);
            }
        }
    }

    /**
     * 查询附件
     *
     * @param id 文件id
     * @return
     */
    @Override
    public Optional<FileDocument> getById(String id) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, COLLECTION_NAME);
        if (fileDocument != null) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(fileDocument.getGridfsId()));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);

            if (fsFile == null || fsFile.getObjectId() == null) {
                return Optional.empty();
            }

            // 开启文件下载
            GridFSDownloadOptions gridFSDownloadOptions = new GridFSDownloadOptions();

            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                } else {
                    return Optional.empty();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 查询附件
     *
     * @param id 文件id
     * @return
     */
    @Override
    public Optional<FileDocument> getPreviewById(String id) {
        FileDocument fileDocument = new FileDocument();
        if (fileDocument != null) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(id));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);

            if (fsFile == null) {
                return Optional.empty();
            }

            fileDocument.setSize(fsFile.getLength());
            fileDocument.setName(fsFile.getFilename());

            if (fsFile == null || fsFile.getObjectId() == null) {
                return Optional.empty();
            }

            // 开启文件下载
            GridFSDownloadOptions gridFSDownloadOptions = new GridFSDownloadOptions();

            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                } else {
                    return Optional.empty();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 根据md5获取文件对象
     *
     * @param md5 String
     * @return -> FileDocument
     */
    @Override
    public FileDocument getByMd5(String md5) {
        if (md5 == null) {
            return null;
        }
        Query query = new Query().addCriteria(Criteria.where("md5").is(md5));
        return mongoTemplate.findOne(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (long) (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize,String userid){
        Query query = new Query().addCriteria(Criteria.where("userId").is(userid)).with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (long) (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     * @Author luojiarui
     * @Description // 增加过滤条件的分页功能
     * @Date 11:12 下午 2022/6/22
     * @Param [pageIndex, pageSize, ids]
     **/
    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);

        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids,String userId) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids));
        query.addCriteria(Criteria.where("userId").is(userId));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);

        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.with(Sort.unsorted());
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);


        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description 列表；过滤；检索等
     * @Date 11:49 2022/8/6
     * @Param [documentDTO]
     **/
    @Override
    public BaseApiResult list(DocumentDTO documentDTO) {
        long startTime = System.currentTimeMillis();

        List<DocumentVO> documentVos;
        List<FileDocument> fileDocuments = Lists.newArrayList();

        PermissionEnum permissionEnum = documentDTO.getPermission();
        String userid = documentDTO.getUserId();
        long totalNum = 0L;

        switch (documentDTO.getType()) {
            case ALL:

                if(permissionEnum == PermissionEnum.USER)
                {
                    fileDocuments = listFilesByPage(documentDTO.getPage(), documentDTO.getRows(),userid);
                    totalNum = fileDocuments.size();
                }
//                管理员可以看到所有用户上传的
                else {
                    fileDocuments = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
                    totalNum = countAllFile();
                }
                break;
            case TAG:
                Tag tag = tagServiceImpl.queryByTagId(documentDTO.getTagId());
                if (tag == null) {
                    break;
                }
                List<String> fileIdList1 = tagServiceImpl.queryDocIdListByTagId(tag.getId());
//                普通用户
                if (permissionEnum == PermissionEnum.USER){
                    fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList1,userid);
                }
//                管理员
                else {
                    fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList1);
                }


                if (CollectionUtils.isEmpty(fileIdList1)) {
                    break;
                }
                Query query = new Query().addCriteria(Criteria.where("_id").in(fileIdList1));
                totalNum = countFileByQuery(query);
                break;
            case FILTER:
                Set<String> docIdSet = new HashSet<>();
                String keyWord = Optional.of(documentDTO).map(DocumentDTO::getFilterWord).orElse("");
//                keyWord = keyWord.replaceAll("\\s+", "");
//                docIdSet.addAll(categoryServiceImpl.fuzzySearchDoc(keyWord));
//                // 模糊查询 标签
//                docIdSet.addAll(tagServiceImpl.fuzzySearchDoc(keyWord));
//                // 模糊查询 文件标题
//                docIdSet.addAll(fuzzySearchDoc(keyWord));
//                // 模糊查询 评论内容
//
//                docIdSet.addAll(commentServiceImpl.fuzzySearchDoc(keyWord));
                List<FileDocument> esDoc = null;

                try {
//                    List<EsSearch> esSearchList = elasticServiceImpl.search_new(keyWord);
                    List<EsSearch> esSearchList = elasticServiceImpl.search_new(keyWord);
                    for(EsSearch esSearch:esSearchList)
                    {
                        if(esSearch.getEsSearchOcrOutcomeList() != null)
                        {
                            esSearch.setOcrResultList(OcrResultFromDB(esSearch,keyWord));
                        }
                        esSearch.setLike_num(getLikeNumByDocId(esSearch.getFileId()));
//                        esSearch
                    }
//                    QuantileNormalization.quantileNormalize(esSearchList,"like",0,30,false);
//                    QuantileNormalization.quantileNormalize(esSearchList,"click",0,10,false);

                    QuantileNormalization.linearNormalize(esSearchList,"like",0,30,false);
                    QuantileNormalization.linearNormalize(esSearchList,"click",0,10,false);
//                    将es的查询结果转为一个List<fileDocument>
                    esDoc = getListFileDocumentFromEsOutcome(esSearchList);

                    setUserNameFromDB(esDoc);

                    esDoc = getThumbIdAndDateFromDB(esDoc);

                    if (!CollectionUtils.isEmpty(esDoc)) {
                        Set<String> existIds = esDoc.stream().map(FileDocument::getId).collect(Collectors.toSet());

                        docIdSet.removeAll(existIds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), docIdSet);
                if (esDoc != null) {
                    fileDocuments = Optional.ofNullable(fileDocuments).orElse(new ArrayList<>());
                    fileDocuments.addAll(esDoc);
                    totalNum = fileDocuments.size();
                }
                break;
            case CATEGORY:
                Category category = categoryServiceImpl.queryById(documentDTO.getCategoryId());
                if (category == null) {
                    break;
                }
                List<String> fileIdList = categoryServiceImpl.queryDocListByCategory(category);

//                普通用户
                if(permissionEnum == PermissionEnum.USER){
                    fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList,userid);
                }
//                管理员
                else {
                    fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList);
                }

                if (CollectionUtils.isEmpty(fileIdList)) {
                    break;
                }
                Query query1 = new Query().addCriteria(Criteria.where("_id").in(fileIdList));
                totalNum = countFileByQuery(query1);
                break;
            default:
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        documentVos = convertDocuments(fileDocuments);
        Map<String, Object> result = new HashMap<>(8);
        result.put("totalNum", totalNum);
        result.put("documents", documentVos);

        long endTime = System.currentTimeMillis();

        System.out.println("startTime:"+startTime);
        System.out.println("endTime:"+endTime);
        System.out.println("查询的时间为：" + (double) (endTime - startTime) / 1000 + "s");
        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult list_advance(AdvanceDocumentDTO advanceDocumentDTO) throws ParseException {
        int page = advanceDocumentDTO.getPage();
        int row = advanceDocumentDTO.getRows();

        List<String> advanceString = getSearchWord(advanceDocumentDTO.getKeyword());

        List<DocumentVO> documentVos;

        int totalNum = 0;

//        首先通过组合条件进行查询,如果组合查询条件不为空
        if(!(advanceDocumentDTO.getKeyword() == null)){

            List<FileDocument> fileDocuments = Lists.newArrayList();

            Set<String> docIdSet = new HashSet<>();
            String keyWord = Optional.of(advanceDocumentDTO).map(AdvanceDocumentDTO::getKeyword).orElse("");
            // 模糊查询 分类
            docIdSet.addAll(categoryServiceImpl.fuzzySearchDoc(keyWord));
            // 模糊查询 标签
            docIdSet.addAll(tagServiceImpl.fuzzySearchDoc(keyWord));
            // 模糊查询 文件标题
            docIdSet.addAll(fuzzySearchDoc(keyWord));
            // 模糊查询 评论内容

            docIdSet.addAll(commentServiceImpl.fuzzySearchDoc(keyWord));
            List<FileDocument> esDoc = null;

            try {
                List<EsSearch> esSearchList = elasticServiceImpl.search_advance(keyWord);
                for(EsSearch esSearch:esSearchList)
                {
                    if(esSearch.getEsSearchOcrOutcomeList() != null)
                    {
                        esSearch.setOcrResultList(OcrResultFromDB(esSearch,advanceString));
                    }
                    esSearch.setLike_num(getLikeNumByDocId(esSearch.getFileId()));
                }

                QuantileNormalization.linearNormalize(esSearchList,"like",0,30,false);
                QuantileNormalization.linearNormalize(esSearchList,"click",0,10,false);

//                    将es的查询结果转为一个List<fileDocument>
                esDoc = getListFileDocumentFromEsOutcome(esSearchList);
                esDoc = getThumbIdAndDateFromDB(esDoc);

                if (!CollectionUtils.isEmpty(esDoc)) {
                    Set<String> existIds = esDoc.stream().map(FileDocument::getId).collect(Collectors.toSet());

                    docIdSet.removeAll(existIds);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileDocuments = listAndFilterByPage(advanceDocumentDTO.getPage(), advanceDocumentDTO.getRows(), docIdSet);
            if (esDoc != null) {
                fileDocuments = Optional.ofNullable(fileDocuments).orElse(new ArrayList<>());
                fileDocuments.addAll(esDoc);
                totalNum = fileDocuments.size();
            }

            List<FileDocument> fileDocuments_filter_title = new ArrayList<>();
//          没有搜索标题
            if(advanceDocumentDTO.getTitle().equals("") || advanceDocumentDTO.getTitle() == null){
                fileDocuments_filter_title = fileDocuments;
            }
//            否则搜索了标题就按照标题来过滤
            else {
                for(FileDocument fileDocument:fileDocuments){
                    String title = fileDocument.getName();
                    if(title.contains(advanceDocumentDTO.getTitle())){
                        fileDocuments_filter_title.add(fileDocument);
                    }
                }
            }

            List<FileDocument> fileDocument_filter_time = new ArrayList<>();
//            如果没有按照时间进行查找
            if(advanceDocumentDTO.getTime()==0)
            {
                fileDocument_filter_time = fileDocuments_filter_title;
            }
//            按照时间进行查找
            else {

                int year_to_compare = advanceDocumentDTO.getTime();
                System.out.println("他要过滤的时间为："+year_to_compare);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
//                Date dateTemp = sdf.parse(year_to_compare);
                Date dateTemp = transformIntToDate(year_to_compare);

                for(FileDocument fileDocument:fileDocuments){
                    Date date = fileDocument.getUploadDate();
                    int result = date.compareTo(dateTemp);
//                    表示年份比用户输入晚
                    if(result > 0){
                        fileDocument_filter_time.add(fileDocument);
                    }
//                    否则表示比用户输入早
                }
            }

            documentVos = convertDocuments(fileDocument_filter_time);
            Map<String, Object> result = new HashMap<>(8);
            result.put("totalNum", totalNum);
            result.put("documents", documentVos);

            return BaseApiResult.success(result);
        }
//        针对于标题进行查询
        else if(!(advanceDocumentDTO.getTitle().equals("") || advanceDocumentDTO.getTitle() == null))
        {
            String title = advanceDocumentDTO.getTitle();

            Query query = new Query();
            if (StringUtils.hasText(title)) {
                Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
                query.addCriteria(Criteria.where("name").regex(pattern));

            }

            int year = advanceDocumentDTO.getTime();
            Date dateTemp = transformIntToDate(year);

            query.addCriteria(Criteria.where(UPLOAD_DATE_FILED_NAME).gt(dateTemp));

            // 不包含该字段
            query.fields().exclude(EXCLUDE_FIELD);

            // 设置起始页和每页查询条数
            Pageable pageable = PageRequest.of(page, row);
            query.with(pageable);
            query.with(Sort.by(Sort.Direction.DESC, "uploadDate"));

            List<FileDocument> fileDocuments = mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);;

            documentVos = convertDocuments(fileDocuments);
            Map<String, Object> result = new HashMap<>(8);
            result.put("totalNum", totalNum);
            result.put("documents", documentVos);

            return BaseApiResult.success(result);
        }
//        只查询时间
        else if(advanceDocumentDTO.getTime() != 0){
            return null;
        }
        else
            return BaseApiResult.error(200,"所以你输入了个啥？");


    }

    private Date transformIntToDate(int yearToCompare) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                Date dateTemp = sdf.parse(String.valueOf(yearToCompare));
                return dateTemp;
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 查询文档的详细信息
     * @Date 9:27 下午 2022/6/23
     * @Param [id]
     **/
    @Override
    public BaseApiResult detail(String id,String username) throws IOException {
        FileDocument fileDocument = queryById(id);
        boolean hasLike,hasCollect;

        LikeDocRelationship likeDB = likeService.getExistLikeRelationship(username,id);
        if(likeDB == null){
            hasLike = false;
        }
        else
            hasLike = true;

        CollectDocRelationship collect = new CollectDocRelationship();
        collect.setUserId(username);
        collect.setDocId(id);
        CollectDocRelationship collectDocRelationship = collectServiceImpl.getExistRelationship(collect);
        if(collectDocRelationship == null){
            hasCollect = false;
        }
        else
            hasCollect = true;

        fileDocument.setHasCollect(hasCollect);
        fileDocument.setHasLike(hasLike);

        elasticServiceImpl.NumberOperation(id,"ADD","click_rate");
        if (fileDocument == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
        } else {
            try {
                redisService.incrementScoreByUserId(id, RedisServiceImpl.DOC_KEY);
            } catch (RedisConnectionFailureException e) {
                log.error("连接redis失败，暂时无法写入数据库", e);
            }
        }
        // 查询评论信息，查询分类信息，查询分类关系，查询标签信息，查询标签关系信息
        return BaseApiResult.success(convertDocument(null, fileDocument));
    }

    @Override
    public BaseApiResult remove(String id) {
        if (!isExist(id)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 删除评论信息，删除分类关系，删除标签关系
        removeFile(id, true);
        commentServiceImpl.removeByDocId(id);
        categoryServiceImpl.removeRelateByDocId(id);
        collectServiceImpl.removeRelateByDocId(id);
        tagServiceImpl.removeRelateByDocId(id);
        elasticServiceImpl.deleteByDocId(id);

        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 查询不同分类条件的文档列表
     * @Date 22:44 2022/11/15
     * @Param [documentDTO]
     **/
    @Override
    public BaseApiResult listWithCategory(DocumentDTO documentDTO) {
        String restrictId;
        String filterWord = documentDTO.getFilterWord();
        int page = documentDTO.getPage();
        int row = documentDTO.getRows();

        List<FileDocument> fileDocuments ;


        if(documentDTO.getPermission() == PermissionEnum.USER){
            System.out.println("普通用户查看");
            fileDocuments = fuzzySearchDocWithPage(filterWord, page, row,documentDTO.getUserId());
        }
        else{
            fileDocuments = fuzzySearchDocWithPage(filterWord, page, row);
            System.out.println("管理员查看的文档数量为"+fileDocuments.size());
        }

        long totalNum = fileDocuments.size();

        List<DocWithCateVO> documentVos = Lists.newArrayList();
        switch (documentDTO.getType()) {
            case CATEGORY:
                restrictId = documentDTO.getCategoryId();
                for (FileDocument fileDocument : fileDocuments) {
                    boolean relateExist = categoryServiceImpl.relateExist(restrictId, fileDocument.getId());
                    documentVos.add(entityTransfer(relateExist, fileDocument));
                }
                break;
            case TAG:
                restrictId = documentDTO.getTagId();
                for (FileDocument fileDocument : fileDocuments) {
                    boolean relateExist = tagServiceImpl.relateExist(restrictId, fileDocument.getId());
                    documentVos.add(entityTransfer(relateExist, fileDocument));
                }
                break;
            default:
                break;
        }
        Map<String, Object> result = new HashMap<>(8);
        result.put("totalNum", totalNum);
        result.put("documents", documentVos);
        return BaseApiResult.success(result);
    }

    /**
     * 根据搜索条件进行模糊查询
     *
     * @param keyWord 关键字
     * @return -> List<FileDocument>
     * @since 2022年11月16日
     */
    private List<FileDocument> fuzzySearchDocWithPage(String keyWord, int page, int row) {
        Query query = new Query();
        if (StringUtils.hasText(keyWord)) {
            Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
        }
        // 不包含该字段
        query.fields().exclude(EXCLUDE_FIELD);

        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(page, row);
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    private List<FileDocument> fuzzySearchDocWithPage(String keyWord, int page, int row,String userid){
        Query query = new Query();
        if (StringUtils.hasText(keyWord)) {
            Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
        }
        query.addCriteria(Criteria.where("userId").is(userid));
        // 不包含该字段
        query.fields().exclude(EXCLUDE_FIELD);

        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(page, row);
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }
    /**
     * @return long
     * @Author luojiarui
     * @Description 符合关键字的总数查询
     * @Date 21:55 2022/11/17
     * @Param [keyWord]
     **/
    private long countNumByKeyWord(String keyWord) {
        if (StringUtils.hasText(keyWord)) {
            Query query = new Query();
            Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
            return mongoTemplate.count(query, COLLECTION_NAME);
        } else {
            return countAllFile();
        }
    }

    private DocWithCateVO entityTransfer(boolean checkState, FileDocument fileDocument) {
        DocWithCateVO doc = new DocWithCateVO();
        String docId = fileDocument.getId();
        doc.setId(docId);
        doc.setCategoryVO(categoryServiceImpl.queryByDocId(docId));
        doc.setSize(fileDocument.getSize());
        doc.setCreateTime(fileDocument.getUploadDate());
        doc.setTitle(fileDocument.getName());
        doc.setTagVOList(tagServiceImpl.queryByDocId(docId));
        doc.setUserName("admin");
        doc.setChecked(checkState);
        return doc;
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.vo.DocumentVO>
     * @Author luojiarui
     * @Description convertDocuments
     * @Date 10:16 下午 2022/6/21
     * @Param fileDocuments
     **/
    private List<DocumentVO> convertDocuments(List<FileDocument> fileDocuments) {
        if (fileDocuments == null) {
            return Lists.newArrayList();
        }
        List<DocumentVO> documentVos = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocuments) {
            DocumentVO documentVO = new DocumentVO();
            documentVO = convertDocument(documentVO, fileDocument);
            documentVos.add(documentVO);
        }

        // 对documentVos按照sum_score降序排序
        List<DocumentVO> sortedDocumentVos = documentVos.stream()
                .sorted(Comparator.comparing(DocumentVO::getSum_score).reversed())
                .collect(Collectors.toList());

        return sortedDocumentVos;
    }

    /**
     * @return com.jiaruiblog.entity.vo.DocumentVO
     * @Author luojiarui
     * @Description convertDocument
     * @Date 10:24 下午 2022/6/21
     * @Param [documentVO, fileDocument]
     **/
    public DocumentVO convertDocument(DocumentVO documentVO, FileDocument fileDocument) {
        documentVO = Optional.ofNullable(documentVO).orElse(new DocumentVO());
        if (fileDocument == null) {
            return documentVO;
        }
        String username = fileDocument.getUserName();
        if (!StringUtils.hasText(username)) {
            username = "未知用户";
        }
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName(username);
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());

        documentVO.setStringList(fileDocument.getDescription_highLighter());

        // 根据文档的id进行查询 评论， 收藏，分类， 标签
        String docId = fileDocument.getId();
        documentVO.setCommentNum(commentServiceImpl.commentNum(docId));
        documentVO.setCollectNum(collectServiceImpl.collectNum(docId));
        documentVO.setCategoryVO(categoryServiceImpl.queryByDocId(docId));
        documentVO.setLikeNum(queryLikeNumByDocId(docId));
        documentVO.setTagVOList(tagServiceImpl.queryByDocId(docId));
        // 查询文档的信息:新增文档地址，文档错误信息，文本id
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());
        documentVO.setPreviewFileId(fileDocument.getPreviewFileId());

        documentVO.setOcrResultList(fileDocument.getOcrResultList());
        documentVO.setEsSearchContentList(fileDocument.getEsSearchContentList());
        documentVO.setEsSearchContentList_syno(fileDocument.getEsSearchContentList_syno());

//      得分
        documentVO.setContent_score(fileDocument.getContentScore());
        documentVO.setClick_score(fileDocument.getClickScore());
        documentVO.setLike_score(fileDocument.getLikeScore());
        documentVO.setSum_score(fileDocument.getClickScore()+fileDocument.getContentScore()+fileDocument.getLikeScore());

        documentVO.setHasCollect(fileDocument.isHasCollect());
        documentVO.setHasLike(fileDocument.isHasLike());
        documentVO.setClick_num(fileDocument.getClick_rate());

        return documentVO;
    }

    private Long queryLikeNumByDocId(String docId){
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return mongoTemplate.count(query,LikeDocRelationship.class,"likeCollection");
    }
    /**
     * 模糊搜索
     *
     * @param keyWord 关键字
     * @return -> List<String>
     */
    public List<String> fuzzySearchDoc(String keyWord) {
        if (!StringUtils.hasText(keyWord)) {
            return Lists.newArrayList();
        }
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));

        List<FileDocument> documents = mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
        return documents.stream().map(FileDocument::getId).collect(Collectors.toList());
    }


    /**
     * 根据用户的主键id查询用户信息
     * //Pattern pattern=Pattern.compile("^.*"+pattern_name+".*$", Pattern.CASE_INSENSITIVE);
     * //query.addCriteria(Criteria.where("name").regex(pattern))；
     *
     * @param docId 文档id
     * @return boolean
     */
    public boolean isExist(String docId) {
        if (!StringUtils.hasText(docId)) {
            return false;
        }
        FileDocument fileDocument = queryById(docId);
        return fileDocument != null;
    }

    /**
     * 检索已经存在的user
     *
     * @param docId String
     * @return FileDocument
     */
    @Override
    public FileDocument queryById(String docId) {
        return mongoTemplate.findById(docId, FileDocument.class, COLLECTION_NAME);
    }


    /**
     * @return java.lang.Integer
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     **/
    public long countAllFile() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    /**
     * @Author luojiarui
     * @Description //转换pdf文档的图片，然后保存
     * @Date 7:49 下午 2022/7/24
     * @Param [inputStream, fileDocument]
     **/
    @Async
    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException {
        // 新建pdf文件的路径
        String path = "thumbnail";
        String picPath = path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png";
        String gridfsId = IdUtil.simpleUUID();
        if (PDF_SUFFIX.equals(fileDocument.getSuffix())) {
            // 将pdf输入流转换为图片并临时保存下来
            PdfUtil.pdfThumbnail(inputStream, picPath);

            if (new File(picPath).exists()) {
                String contentType = "image/png";
                FileInputStream in = new FileInputStream(picPath);
                //文件，存储在GridFS
                gridFsTemplate.store(in, gridfsId, contentType);
                try {
                    Files.delete(Paths.get(picPath));
                } catch (IOException e) {
                    log.error("删除文件路径{} ==> 失败信息{}", picPath, e);
                }
            }
        }

        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update().set("thumbId", gridfsId);
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

    }

    /**
     * @return java.io.InputStream
     * @Author luojiarui
     * @Description //根据缩略图id返回图片信息
     * @Date 7:59 下午 2022/7/24
     * @Param [thumbId]
     **/
    @Override
    public InputStream getFileThumb(String thumbId) {
        if (StringUtils.hasText(thumbId)) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(thumbId));
//            Query gridQuery = new Query().addCriteria(Criteria.where("_id").is(thumbId));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            if (fsFile == null) {
                return null;
            }
            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    return resource.getInputStream();
                } else {
                    return null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public byte[] getFileBytes(String thumbId) {

        if (StringUtils.hasText(thumbId)) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(thumbId));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            if (fsFile == null) {
                return new byte[0];
            }
            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    return IoUtil.readBytes(resource.getInputStream());
                } else {
                    return new byte[0];
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new byte[0];
    }

    /**
     * @return long
     * @Author luojiarui
     * @Description 根据查询条件查询总数量
     * @Date 12:09 2022/8/6
     * @Param [query]
     **/
    public long countFileByQuery(Query query) {
        return mongoTemplate.count(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return com.jiaruiblog.entity.FileDocument
     * @Author luojiarui
     * @Description 查询并删除文档
     * @Date 10:07 2022/12/10
     * @Param [docId]
     **/
    @Override
    public List<FileDocument> queryAndRemove(String... docId) {
        if (CollectionUtils.isEmpty(Arrays.asList(docId))) {
            return null;
        }
        Query query = new Query(Criteria.where("_id").in(docId));
        return mongoTemplate.findAllAndRemove(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     * @Author luojiarui
     * @Description 修改并返回查询到的文档信息
     * @Date 10:31 2022/12/10
     * @Param [docId]
     **/
    @Override
    public List<FileDocument> queryAndUpdate(String... docId) {
        if (CollectionUtils.isEmpty(Arrays.asList(docId))) {
            return Collections.emptyList();
        }
        Query query = new Query(Criteria.where("_id").in(docId));
        Update update = new Update();
        update.set("reviewing", false);
        mongoTemplate.updateMulti(query, update, COLLECTION_NAME);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing) {

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        query.addCriteria(Criteria.where("reviewing").is(reviewing));
        int pageIndex = pageDTO.getPage();
        int pageSize = pageDTO.getRows();
        long skip = (long) (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude(CONTENT);

        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public BaseApiResult queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        query.addCriteria(Criteria.where("reviewing").is(reviewing));
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", queryFileDocument(pageDTO, reviewing));
        result.put("total", mongoTemplate.count(query, FileDocument.class, COLLECTION_NAME));
        return BaseApiResult.success(result);
    }

    private List<OcrResult> OcrResultFromDB(EsSearch esSearch,String keyword){
        List<EsSearchOcrOutcome> esSearchOcrOutcomeList = esSearch.getEsSearchOcrOutcomeList();
        List<OcrResult> list = new ArrayList<>();

        for(EsSearchOcrOutcome esSearchOcrOutcome:esSearchOcrOutcomeList)
        {
            if(esSearchOcrOutcome.getOcrText() != null)
            {
                String mongoDB_id = esSearchOcrOutcome.getMongoDbId();

                Query query = new Query(Criteria.where("_id").in(mongoDB_id));
                OcrResult ocrResults = mongoTemplate.findOne(query, OcrResult.class, OCR_RESULT_NAME);

                List<OcrPosition> ocrPositionList = ocrResults.getTextResult();
                List<OcrPosition> ocrPositionList_new = new ArrayList<>();
                for(int i=0;i<ocrPositionList.size();i++)
                {
                    String ocrPositionTemp = ocrPositionList.get(i).getText();
//                    如果不包括关键字，则剔除该项
                    if(ocrPositionTemp.contains(keyword))
                    {
                        ocrPositionList_new.add(ocrPositionList.get(i));
                    }
                }
                ocrResults.setTextResult(ocrPositionList_new);
                list.add(ocrResults);
            }
        }
        return list;
    }

    private List<OcrResult> OcrResultFromDB(EsSearch esSearch,List<String> keywords){
        List<EsSearchOcrOutcome> esSearchOcrOutcomeList = esSearch.getEsSearchOcrOutcomeList();
        List<OcrResult> list = new ArrayList<>();

        for(EsSearchOcrOutcome esSearchOcrOutcome:esSearchOcrOutcomeList)
        {
            if(esSearchOcrOutcome.getOcrText() != null)
            {
                String mongoDB_id = esSearchOcrOutcome.getMongoDbId();

                Query query = new Query(Criteria.where("_id").in(mongoDB_id));
                OcrResult ocrResults = mongoTemplate.findOne(query, OcrResult.class, OCR_RESULT_NAME);

                List<OcrPosition> ocrPositionList = ocrResults.getTextResult();
                List<OcrPosition> ocrPositionList_new = new ArrayList<>();
                for(int i=0;i<ocrPositionList.size();i++)
                {
                    String ocrPositionTemp = ocrPositionList.get(i).getText();

                    boolean flag = false;
                    for(String keyword:keywords){
                        if(ocrPositionTemp.contains(keyword))
                        {
                            ocrPositionList_new.add(ocrPositionList.get(i));
                            flag = true;
                            break;
                        }
                    }

                }
                ocrResults.setTextResult(ocrPositionList_new);
                list.add(ocrResults);
            }
        }
        return list;
    }

    private List<FileDocument> getListFileDocumentFromEsOutcome(List<EsSearch> esSearchList){

        List<FileDocument> fileDocuments = new ArrayList<>();
        for(EsSearch esSearch:esSearchList){
            FileDocument fileDocument = new FileDocument();
//            设置ocr结果
            fileDocument.setOcrResultList(esSearch.getOcrResultList());
//            设置es搜索结果的content
            fileDocument.setEsSearchContentList(esSearch.getEsSearchContentList());
            fileDocument.setEsSearchContentList_syno(esSearch.getEsSearchContentList_syno());

            fileDocument.setId(esSearch.getId());
            fileDocument.setName(esSearch.getName());
            fileDocument.setMd5(esSearch.getMd5());
            fileDocument.setContentType(esSearch.getType());
            fileDocument.setContentScore(esSearch.getContentScore());
            fileDocument.setLikeScore(esSearch.getLikeScore());
            fileDocument.setClickScore(esSearch.getClickScore());

            fileDocument.setThumbId(esSearch.getThumbId());
            fileDocument.setUploadDate(esSearch.getDate());

            fileDocument.setClick_rate(esSearch.getClick_num());

            fileDocuments.add(fileDocument);
        }
        return fileDocuments;
    }

    private List<FileDocument> getThumbIdAndDateFromDB(List<FileDocument> esDocs){

        for(FileDocument esDoc:esDocs) {
//            System.out.println("md5:" + esDoc.getMd5());
            Query query = new Query(Criteria.where("_id").is(esDoc.getId()));
            query.fields().include(UPLOAD_DATE_FILED_NAME).include(THUMBID_FILED_NAME);

            ThumbIdAndDate document = mongoTemplate.findOne(query, ThumbIdAndDate.class, COLLECTION_NAME);

            esDoc.setUploadDate(document.getUploadDate());
            esDoc.setThumbId(document.getThumbId());
        }

        return esDocs;
    }

    @Override
    public ResponseModel createScanPDF(String filename, MultipartFile[] files, HttpServletRequest request) throws DocumentException, IOException, AuthenticationException {

//        使用Uitl工具中的方法将图片转为PDF
        MultipartFile fileScan = ImageToPdfConverter.convertImagesToPdf(files,filename);

        InputStream inputStream = fileScan.getInputStream();
        String UUID = uploadFileToGridFs(inputStream,fileScan.getContentType());

        CallFlask callFlask = new CallFlask();

        String fileScanPath = callFlask.toScan(fileScan,filename);

        File file = new File(fileScanPath);
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();

        // Convert the byte array to a MultipartFile object
        MultipartFile multipartFile = new CustomMultipartFile(file.getName(), "application/pdf", fileBytes);
        UploadFileObj uploadFileObj = new UploadFileObj();
        String userId = (String) request.getAttribute("id");
        String username = (String) request.getAttribute("username");
        uploadFileObj.setUserId(userId);
        uploadFileObj.setUsername(username);
        uploadFileObj.setFile(multipartFile);

        return documentUpload_noAuth(uploadFileObj);
    }

    private void setUserNameFromDB(List<FileDocument> fileDocumentList){
        for(FileDocument fileDocument:fileDocumentList){
            Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
            FileDocument result = mongoTemplate.findOne(query, FileDocument.class,COLLECTION_NAME);
            fileDocument.setUserName(result.getUserName());
        }
    }
    private List<String> getSearchWord(String advanceWords){

        List<String> resultStrings = new ArrayList<>();

        // Remove parentheses, ampersands, and vertical bars from the input string
        String filteredString = advanceWords.replaceAll("[()&|]", "");

         // Split the filtered string by whitespace to get individual Chinese strings
        String[] splitStrings = filteredString.split("\\s+");

        // Add each Chinese string to the list
        for (String s : splitStrings) {
            if(StringUtils.hasText(s))
                resultStrings.add(s);
        }

        // Print the resulting list of Chinese strings
        System.out.println(resultStrings);

        return resultStrings;
    }
    private Long getLikeNumByDocId(String docId){
        System.out.println("fileId是："+docId);
        return likeService.likeNum(docId);
    }

    private Long getCollectNumByDocId(String docId){
        return collectServiceImpl.collectNum(docId);
    }

//    private static class CustomMultipartFile implements MultipartFile {
//
//        private final String name;
//        private final String contentType;
//        private final byte[] content;
//
//        public CustomMultipartFile(String name, String contentType, byte[] content) {
//            this.name = name;
//            this.contentType = contentType;
//            this.content = content;
//        }
//
//        @Override
//        public String getName() {
//            return name;
//        }
//
//        @Override
//        public String getOriginalFilename() {
//            return name;
//        }
//
//        @Override
//        public String getContentType() {
//            return contentType;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return content == null || content.length == 0;
//        }
//
//        @Override
//        public long getSize() {
//            return content.length;
//        }
//
//        @Override
//        public byte[] getBytes() throws IOException {
//            return content;
//        }
//
//        @Override
//        public InputStream getInputStream() throws IOException {
//            return new ByteArrayInputStream(content);
//        }
//
//        @Override
//        public void transferTo(File dest) throws IOException, IllegalStateException {
//            Files.write(dest.toPath(), content);
//        }
//    }

}
